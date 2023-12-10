import json


def to_sns_message(id, uuid, sender, text, timestamp):
    return {
        'Id': str(id),
        'Message': json.dumps({
            'uuid': uuid,
            'sender': sender,
            'text': text,
            'timestamp': timestamp,
        }),
    }


def publish_sns_batch(sns_batch_request_entries, sns_client):
    sns_client.publish_batch(
        TopicArn='arn:aws:sns:us-east-1:661790212808:whispers-back-dev-whisper-created',
        PublishBatchRequestEntries=sns_batch_request_entries,
    )
    print('published batch of SNS messages')
