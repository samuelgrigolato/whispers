# Whispers - Python Lambdas

## Useful Shell Commands

### Running lambda_feed_query.py Locally

- Postgres, local:

```
STORAGE_TYPE=PG \
PG_CONN_STR="dbname=whispers user=postgres password=123456 host=127.0.0.1" \
PG_THREADS=50 \
PG_QUERIES_PER_THREAD=1500 \
python lambda_feed_query.py
```

- Postgres, RDS:

Note: remember to check your IP address and update the security group accordingly.

```
STORAGE_TYPE=PG \
PG_CONN_STR="dbname=whispers user=postgres password=postgres host=whispers-back-dev.cwzcsyyjtn0f.us-east-1.rds.amazonaws.com" \
PG_THREADS=2 \
PG_QUERIES_PER_THREAD=2 \
python lambda_feed_query.py
```

- DynamoDB, boto3:

```
STORAGE_TYPE=DYNAMODB \
DYNAMODB_CLIENT_TYPE=BOTO3 \
DYNAMODB_THREADS=2 \
DYNAMODB_QUERIES_PER_THREAD=2 \
python lambda_feed_query.py
```

- DynamoDB, DAX:

Note: unfortunately DAX does not work outside of the VPC, so you'll won't be able to use the command below.

```
STORAGE_TYPE=DYNAMODB \
DYNAMODB_CLIENT_TYPE=DAX \
DYNAMODB_DAX_ENDPOINT=daxs://whispers-back-dev.aopd1z.dax-clusters.us-east-1.amazonaws.com \
DYNAMODB_THREADS=2 \
DYNAMODB_QUERIES_PER_THREAD=2 \
python lambda_feed_query.py
```

### Running lambda_whisper_generator.py Locally

- Postgres, local:

```
STORAGE_TYPE=PG \
PG_CONN_STR="dbname=whispers user=postgres password=123456 host=127.0.0.1" \
PG_USERS=1 \
PG_WHISPERS=4 \
PG_WHISPERS_PER_BATCH=2 \
python lambda_whisper_generator.py
```

- Postgres, RDS:

Note: remember to check your IP address and update the security group accordingly.

```
STORAGE_TYPE=PG \
PG_CONN_STR="dbname=whispers user=postgres password=postgres host=whispers-back-dev.cwzcsyyjtn0f.us-east-1.rds.amazonaws.com" \
PG_USERS=2 \
PG_WHISPERS=2 \
PG_WHISPERS_PER_BATCH=2 \
python lambda_whisper_generator.py
```

- DynamoDB, boto3:

```
STORAGE_TYPE=DYNAMODB \
DYNAMODB_USERS=10 \
DYNAMODB_WHISPERS=100 \
DYNAMODB_GLOBAL_BUCKETS=10 \
python lambda_whisper_generator.py
```

### Invoking Lambdas

- Single invocation:

```
./invoker feed-query 1
```

- Single invoker, two invocations:

```
./invoker feed-query 2
```

- Two invokers, five invocations per invoker:

```
./multi_invoker feed-query 2 5
```
