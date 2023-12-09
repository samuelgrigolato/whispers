import boto3
import threading
from amazondax import AmazonDaxClient


def handler(event, context):
    ts = []
    for _ in range(100):
        t = threading.Thread(target=query_feed)
        t.start()
        ts.append(t)
    for t in ts:
        t.join()


def query_feed():
    # dynamo_client = boto3.client('dynamodb')
    # works only on VPC
    dynamo_client = AmazonDaxClient(
        endpoints=['daxs://whispers-back-dev.aopd1z.dax-clusters.us-east-1.amazonaws.com'],
    )
    for i in range(25):
        print(f"querying for the {i} time")
        results = dynamo_client.query(
            TableName='whispers-back-dev',
            IndexName='gsi1',
            ScanIndexForward=False,
            Limit=10,
            KeyConditionExpression='gsi1Pk = :pk',
            ExpressionAttributeValues={
                ':pk': {'S': f'global1'},
            },
        )
        for item in results['Items']:
            print(item['pk']['S'])


if __name__ == '__main__':
    handler(None, None)
