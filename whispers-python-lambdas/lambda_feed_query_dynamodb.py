import os
import boto3
import threading
from amazondax import AmazonDaxClient


DYNAMODB_CLIENT_TYPE = os.environ['DYNAMODB_CLIENT_TYPE'].upper() # 'BOTO3' or 'DAX'
if DYNAMODB_CLIENT_TYPE == 'BOTO3':
    dynamo_client = boto3.client('dynamodb')
elif DYNAMODB_CLIENT_TYPE == 'DAX':
    dynamo_client = AmazonDaxClient(
        endpoints=[
            os.environ['DYNAMODB_DAX_ENDPOINT'],
        ],
    )


def handler(event, context):
    ts = []
    for _ in range(int(os.environ[f'DYNAMODB_THREADS'])):
        t = threading.Thread(target=query_feed)
        t.start()
        ts.append(t)
    for t in ts:
        t.join()


def query_feed():
    for i in range(int(os.environ[f'DYNAMODB_QUERIES_PER_THREAD'])):
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
            ReturnConsumedCapacity='INDEXES',
        )
        for item in results['Items']:
            print(item['pk']['S'])


if __name__ == '__main__':
    handler(None, None)
