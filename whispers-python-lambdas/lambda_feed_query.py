import os
import boto3
import threading
from amazondax import AmazonDaxClient
import psycopg2

# dynamo_client = boto3.client('dynamodb')
# works only on VPC
# if you setup the client inside "query_feed" function, it throttles a lot
# dax.r5.xlarge is the smallest instance type that can be used for the 50k requests test
dynamo_client = AmazonDaxClient(
    endpoints=[
        # 'daxs://whispers-back-dev.aopd1z.dax-clusters.us-east-1.amazonaws.com',
        'daxs://whispers-back-dev4.aopd1z.dax-clusters.us-east-1.amazonaws.com',
    ],
)
# conn_str = os.environ['DB_CONN_STR']
# conn = psycopg2.connect(conn_str)


def handler(event, context):
    ts = []
    for _ in range(25):
        t = threading.Thread(target=query_feed)
        t.start()
        ts.append(t)
    for t in ts:
        t.join()


def query_feed():
    for i in range(1500):
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
    # with psycopg2.connect(conn_str) as conn:
    #     with conn.cursor() as cursor:
    #         for i in range(1500):
    #             print(f"querying for the {i} time")
    #             cursor.execute("""
    #                 select id
    #                 from whispers
    #                 limit 10;
    #             """)
    #             results = cursor.fetchall()
    #             for item in results:
    #                 print(item[0])


if __name__ == '__main__':
    handler(None, None)
