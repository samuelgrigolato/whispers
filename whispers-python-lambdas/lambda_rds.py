import os
import psycopg2
from uuid import uuid4
import random
import string
from datetime import datetime, timezone
import boto3

from sns_publisher import to_sns_message, publish_sns_batch

# "dbname=whispers user=postgres password=123456 host=localhost"
conn_str = os.environ['DB_CONN_STR']
conn = psycopg2.connect(conn_str)


def handler(event, context):
    sns_client = boto3.client('sns')
    users = [
        str(uuid4())
        for _ in range(10)
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
    for i in range(1000):
        print(f'inserting whisper {i}')
        uuid = str(uuid4())
        sender = users[i % 10]
        text = f"whisper {random_string(25)} {random_string(25)} {random_string(25)} {random_string(25)}"
        formatted_timestamp = datetime.now(timezone.utc).isoformat()
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    insert into whispers (id, sender_id, text, timestamp)
                    values (%s, %s, %s, %s);
                """, (uuid, sender, text, formatted_timestamp))
        sns_batch_request_entries.append(to_sns_message(i, uuid, sender, text, formatted_timestamp))
        if len(sns_batch_request_entries) == 10:
            publish_sns_batch(sns_batch_request_entries, sns_client)
            sns_batch_request_entries = []
    if len(sns_batch_request_entries) > 0:
        publish_sns_batch(sns_batch_request_entries, sns_client)


def random_string(length):
    return ''.join(random.choice(string.ascii_letters) for i in range(length))


if __name__ == '__main__':
    handler(None, None)
