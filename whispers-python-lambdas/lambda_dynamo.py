import os
from uuid import uuid4
import random
import string
import boto3
from datetime import datetime, timezone


def handler(event, context):
    client = boto3.client('dynamodb')
    users = [
        f"random-{str(uuid4())}"
        for _ in range(10)
    ]

    for i in range(1000):
        uuid = str(uuid4())
        print(f'inserting whisper {i} - {uuid}')
        formatted_timestamp = datetime.now(timezone.utc).isoformat()
        suffixed_timestamp = f'{formatted_timestamp}#{uuid}'
        sender = users[i % 10]
        resp = client.put_item(
            TableName='whispers-back-dev',
            Item={
                'pk': {'S': f'whisper#{uuid}'},
                'sk': {'S': 'entry'},
                'gsi1Pk': {'S': 'global' + str(i % 10)},
                'gsi1Sk': {'S': suffixed_timestamp},
                'gsi2Sk': {'S': suffixed_timestamp},
                'gsi3Pk': {'S': sender},
                'gsi3Sk': {'S': formatted_timestamp},
                'uuid': {'S': uuid},
                'sender': {'S': sender},
                'text': {'S': f"whisper {random_string(25)} {random_string(25)} {random_string(25)} {random_string(25)}"},
                'timestamp': {'S': formatted_timestamp},
                'replies': {'S': '[]'}
            }
        )
        print(f"whisperUuid={uuid} statusCode={resp['ResponseMetadata']['HTTPStatusCode']}")


def random_string(length):
    return ''.join(random.choice(string.ascii_letters) for i in range(length))


if __name__ == '__main__':
    handler(None, None)
