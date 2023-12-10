import os
from uuid import uuid4
import boto3
from datetime import datetime, timezone

from whisper_text_generator import random_text
from whisper_created_sns_publisher import to_sns_message, publish_sns_batch


dynamo_client = boto3.client('dynamodb')
DYNAMODB_USERS = int(os.environ['DYNAMODB_USERS'])
DYNAMODB_WHISPERS = int(os.environ['DYNAMODB_WHISPERS'])
DYNAMODB_GLOBAL_BUCKETS = int(os.environ['DYNAMODB_GLOBAL_BUCKETS'])


def handler(event, context):
    sns_client = boto3.client('sns')
    users = [
        f"random-{str(uuid4())}"
        for _ in range(DYNAMODB_USERS)
    ]
    sns_batch_request_entries = []
    for i in range(DYNAMODB_WHISPERS):
        uuid = str(uuid4())
        print(f'inserting whisper {i} - {uuid}')
        formatted_timestamp = datetime.now(timezone.utc).isoformat()
        suffixed_timestamp = f'{formatted_timestamp}#{uuid}'
        sender = users[i % DYNAMODB_USERS]
        text = random_text()
        resp = dynamo_client.put_item(
            TableName='whispers-back-dev',
            Item={
                'pk': {'S': f'whisper#{uuid}'},
                'sk': {'S': 'entry'},
                'gsi1Pk': {'S': 'global' + str(i % DYNAMODB_GLOBAL_BUCKETS)},
                'gsi1Sk': {'S': suffixed_timestamp},
                'gsi2Sk': {'S': suffixed_timestamp},
                'gsi3Pk': {'S': sender},
                'gsi3Sk': {'S': formatted_timestamp},
                'uuid': {'S': uuid},
                'sender': {'S': sender},
                'text': {'S': text},
                'timestamp': {'S': formatted_timestamp},
                'replies': {'S': '[]'}
            }
        )
        print(f"whisperUuid={uuid} statusCode={resp['ResponseMetadata']['HTTPStatusCode']}")
        sns_batch_request_entries.append(to_sns_message(i, uuid, sender, text, formatted_timestamp))
        if len(sns_batch_request_entries) == 10:
            publish_sns_batch(sns_batch_request_entries, sns_client)
            sns_batch_request_entries = []
    if len(sns_batch_request_entries) > 0:
        publish_sns_batch(sns_batch_request_entries, sns_client)


if __name__ == '__main__':
    handler(None, None)
