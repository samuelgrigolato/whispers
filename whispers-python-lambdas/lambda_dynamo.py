import os
from uuid import uuid4
import random
import string
import boto3
import json
from datetime import datetime, timezone

from sns_publisher import to_sns_message, publish_sns_batch


def handler(event, context):
    dynamo_client = boto3.client('dynamodb')
    if event and 'Records' in event:
        for record in event['Records']:
            sns_message = json.loads(record['body'])
            event = json.loads(sns_message['Message'])
            if isinstance(event, list):
                for trending_topic in event:
                    print(f"ingesting trending topic {trending_topic['topic']} with whisper count {trending_topic['whisperCount']}")
                    topic = trending_topic['topic']
                    whisper_count_str = str(trending_topic['whisperCount'])
                    sk = ('00000000000000000000' + whisper_count_str)[-20:] + '#' + topic
                    dynamo_client.put_item(
                        TableName='whispers-back-dev',
                        Item={
                            'pk': {'S': f'trending-topics'},
                            'sk': {'S': sk},
                            'topic': {'S': topic},
                            'whisperCount': {'N': whisper_count_str},
                        }
                    )
            else:
                print(f"updating whisper {event['whisperUuid']} with topic {event['topic']}")
                whisper_uuid = event['whisperUuid']
                topic = event['topic']
                dynamo_client.update_item(
                    TableName='whispers-back-dev',
                    Key={
                        'pk': {'S': f'whisper#{whisper_uuid}'},
                        'sk': {'S': 'entry'},
                    },
                    AttributeUpdates={
                        'topic': {
                            'Value': {'S': topic},
                            'Action': 'PUT',
                        },
                        'gsi2Pk': {
                            'Value': {'S': topic},
                            'Action': 'PUT',
                        },
                    },
                )
        return

    sns_client = boto3.client('sns')
    users = [
        f"random-{str(uuid4())}"
        for _ in range(10)
    ]

    sns_batch_request_entries = []
    for i in range(1000):
        uuid = str(uuid4())
        print(f'inserting whisper {i} - {uuid}')
        formatted_timestamp = datetime.now(timezone.utc).isoformat()
        suffixed_timestamp = f'{formatted_timestamp}#{uuid}'
        sender = users[i % 10]
        text = f"whisper {random_string(25)} #random{i}"
        resp = dynamo_client.put_item(
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


def random_string(length):
    return ''.join(random.choice(string.ascii_letters) for i in range(length))


if __name__ == '__main__':
    # whisper generation
    handler(None, None)
    # trending topic ingestion
    # handler({
    #     'Records': [
    #         {
    #             'body': json.dumps({
    #                 'Message': json.dumps([
    #                     {
    #                         'topic': 'topic1',
    #                         'whisperCount': 1000,
    #                     },
    #                     {
    #                         'topic': 'topic2',
    #                         'whisperCount': 200,
    #                     },
    #                 ])
    #             })
    #         }
    #     ]
    # }, None)
    # whisper update
    # handler({
    #     'Records': [
    #         {
    #             'body': json.dumps({
    #                 'Message': json.dumps({
    #                     'whisperUuid': 'b7a0c5e0-7e4e-4c3e-8b4a-9d7d3b7b2a6f',
    #                     'topic': 'topic1',
    #                 })
    #             })
    #         }
    #     ]
    # }, None)
