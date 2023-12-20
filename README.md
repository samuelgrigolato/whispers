# Whispers

Whispers is a fictional short posts social network, built to be presented during DIO's International Acceleration: "Scaling Trending Topics with Spring and AWS".

Link to the video recordings: _pending_.

## Projects

- whispers-front: Vue front-end application.
- whispers-python-lambdas: useful python helper lambdas for performance testing.
- whispers-back: core bounded context.
- trending-topics-back: topics bounded context.

## Running the Front End

```
$ cd whispers-front
$ npm install
$ npm run dev
```

## Running the Back End

### Using Local Infrastructure (docker compose)

```
$ docker compose up
```

Then open `whispers-back` on your IDE and launch `io.whispers.bin.webjpa.Application` or `io.whispers.bin.webdynamo.Application`.

You can also partially run the whispers worker targetting local infrastructure. More especifically, you can target the local Dynamo or PostgreSQL database, but you'll have to use a real AWS queue.

### Using Real AWS Resources

Note: be sure to familiarize yourself with all the `application.properties` (from `whispers-back`) and `configuration.properties` (from `trending-topics-back`) files, as you'll have to adjust them in order to run using the chosen environment combination. Also, remember to replace all the occurrences of the AWS account ID with the correct one.

Hint: to avoid hard to diagnose network issues, use your default VPC and corresponding default security group whenever asked for one.

In order to execute `trending-topics-back`, you'll need real AWS resources. Ensure you have access to a root account and follow the steps below:

1. Create the following SNS topics:

- `whispers-back-dev-whisper-created`
- `trending-topics-dev-topic-resolved`
- `trending-topics-dev-top-10-ranking`

2. Create the SQS queues:

- `trending-topics-dev-whisper-created-failure`
- `whispers-back-dev-topic-resolved-failure`
- `whispers-back-dev-trending-topics-failure`
- `trending-topics-dev-whisper-created`
- `whispers-back-dev-topic-resolved`
- `whispers-back-dev-trending-topics`

Note: select the `-failure` one as the dead-letter queue for the corresponding regular queue.

3. If you want to run `whispers-back#web/worker-jpa` targeting a remote database: create an RDS database using the PostgreSQL engine. You'll then have to setup the security group allowing access from your publicly visible IP address and update the connection properties in the `application.properties` file.
4. If you want to run `whispers-back#web/worker-dynamo` targeting real Dynamo: create a DynamoDB table `whispers-back-dev`. Remember to update the `application.properties` file changing the dynamo endpoint.

    Add three indexes to the table:

    - gsi1: gsi1Pk (partition key, String) / gsi1Sk (sort key, String)
    - gsi2: gsi2Pk (partition key, String) / gsi2Sk (sort key, String)
    - gsi3: gsi3Pk (partition key, String) / gsi3Sk (sort key, String)

5. Create an instance of MemoryDB for Redis

    Take note of the cluster endpoint, as you'll need to update the `configuration.properties` files with it before building the lambda packages below.

6. Create the lambdas below:

    * `trending-topics-dev-top-10-publisher`: triggered by EventBridge using a rate of 1 minute

        Runtime: Java 21
        Handler: `io.whispers.trending.top10publisher.Handler::handleRequest`
        Role permissions: check the "troubleshooting" section below
        Network: all subnets from your default VPC

    * `trending-topics-dev-whisper-created`: triggered by the SQS queue `trending-topics-dev-whisper-created`

        Runtime: Java 21
        Handler: `io.whispers.trending.whispercreated.Handler::handleRequest`
        Role permissions: check the "troubleshooting" section below
        Network: all subnets from your default VPC

    ZIP package: use the gradle task `buildZip` on the appropriate module (`trending-topics-bin-*`) to produce a ZIP file that you can upload.

    Network: you'll need to create a VPC endpoint allowing the lambdas above to reach SNS. Warning: there's no free tier for VPC endpoints!

### Troubleshooting AWS Permission Issues

Ensure that the IAM user you're using to run the lambda and/or the local web/worker-{jpa/dynamo} process has access to the appropriate resources. Here's a list of required permissions:

- trending-topics-back:

    Publish to topic resolved:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": "sns:Publish",
                "Resource": "arn:aws:sns:*::trending-topics-dev-topic-resolved"
            }
        ]
    }
    ```

    Publish to trending topics ranking:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": "sns:Publish",
                "Resource": "arn:aws:sns:*::trending-topics-dev-top-10-ranking"
            }
        ]
    }
    ```

- whispers-back:

    Manage Dynamo table:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": [
                    "dynamodb:BatchGetItem",
                    "dynamodb:PutItem",
                    "dynamodb:DescribeTable",
                    "dynamodb:DeleteItem",
                    "dynamodb:GetItem",
                    "dynamodb:Query",
                    "dynamodb:UpdateItem"
                ],
                "Resource": [
                    "arn:aws:dynamodb:*::table/whispers-back-dev/index/*",
                    "arn:aws:dynamodb:*::table/whispers-back-dev"
                ]
            }
        ]
    }
    ```

    Publish to whisper created:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": "sns:Publish",
                "Resource": "arn:aws:sns:us-east-1::whispers-back-dev-whisper-created"
            }
        ]
    }
    ```

    Receive SQS messages:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": [
                    "sqs:DeleteMessage",
                    "sqs:GetQueueUrl",
                    "sqs:ReceiveMessage",
                    "sqs:GetQueueAttributes"
                ],
                "Resource": "arn:aws:sqs:*::whispers-back-dev-*"
            }
        ]
    }
    ```

- trending-topics-top10-publisher-role

    Publish to top 10 ranking

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": "sns:Publish",
                "Resource": "arn:aws:sns:*::trending-topics-dev-top-10-ranking"
            }
        ]
    }
    ```

- trending-topics-whisper-created-role

    Publish to topic resolved:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": "sns:Publish",
                "Resource": "arn:aws:sns:*::trending-topics-dev-topic-resolved"
            }
        ]
    }
    ```

    Read from source queue:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": [
                    "sqs:ReceiveMessage",
                    "sqs:DeleteMessage",
                    "sqs:GetQueueAttributes"
                ],
                "Resource": "arn:aws:sqs:*::trending-topics-dev-whisper-created"
            }
        ]
    }
    ```
