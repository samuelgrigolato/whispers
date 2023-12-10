import os
import psycopg2
from uuid import uuid4
import boto3
from datetime import datetime, timezone

from whisper_text_generator import random_text
from whisper_created_sns_publisher import to_sns_message, publish_sns_batch


PG_USERS = int(os.environ['PG_USERS'])
PG_WHISPERS = int(os.environ['PG_WHISPERS'])
PG_WHISPERS_PER_BATCH = int(os.environ['PG_WHISPERS_PER_BATCH'])

conn_str = os.environ['PG_CONN_STR']
conn = psycopg2.connect(conn_str)


def handler(event, context):
    sns_client = boto3.client('sns')
    users = [
        str(uuid4())
        for _ in range(PG_USERS)
    ]
    with conn:
        with conn.cursor() as cursor:
            for user in users:
                print(f'inserting user {user}')
                cursor.execute("""
                    insert into users (id, username)
                    values (%s, %s);
                """, (user, f"random-{user}"))

    sns_batch_request_entries = []
    for i in range(PG_WHISPERS // PG_WHISPERS_PER_BATCH):
        for j in range(PG_WHISPERS_PER_BATCH):
            with conn:
                whisper_index = i * PG_WHISPERS_PER_BATCH + j
                sender = users[whisper_index % PG_USERS]
                uuid = str(uuid4())
                text = random_text()
                formatted_timestamp = datetime.now(timezone.utc).isoformat()
                print(f'inserting whisper {whisper_index}')
                with conn.cursor() as cursor:
                    cursor.execute("""
                        insert into whispers (id, sender_id, text, timestamp)
                        values (%s, %s, %s, %s);
                    """, (uuid, sender, text, formatted_timestamp))
                sns_batch_request_entries.append(to_sns_message(whisper_index, uuid, sender, text, formatted_timestamp))
                if len(sns_batch_request_entries) == 10:
                    publish_sns_batch(sns_batch_request_entries, sns_client)
                    sns_batch_request_entries = []
    if len(sns_batch_request_entries) > 0:
        publish_sns_batch(sns_batch_request_entries, sns_client)


if __name__ == '__main__':
    handler(None, None)
