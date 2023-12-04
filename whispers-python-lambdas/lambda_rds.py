import os
import psycopg2
from uuid import uuid4
import random
import string


# "dbname=whispers user=postgres password=123456 host=localhost"
conn_str = os.environ['DB_CONN_STR']
conn = psycopg2.connect(conn_str)


def handler(event, context):
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

    for i in range(1000):
        print(f'inserting whisper {i}')
        with conn:
            with conn.cursor() as cursor:
                cursor.execute("""
                    insert into whispers (id, sender_id, text, timestamp)
                    values (%s, %s, %s, now());
                """, (str(uuid4()), users[i % 10], f"whisper {random_string(25)} {random_string(25)} {random_string(25)} {random_string(25)}"))


def random_string(length):
    return ''.join(random.choice(string.ascii_letters) for i in range(length))


if __name__ == '__main__':
    handler(None, None)
