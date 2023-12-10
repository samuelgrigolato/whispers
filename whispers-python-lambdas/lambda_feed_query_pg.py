import os
import threading
import psycopg2


conn_str = os.environ['PG_CONN_STR']
conn = psycopg2.connect(conn_str)


def handler(event, context):
    ts = []
    for _ in range(int(os.environ[f'PG_THREADS'])):
        t = threading.Thread(target=query_feed)
        t.start()
        ts.append(t)
    for t in ts:
        t.join()


def query_feed():
    with psycopg2.connect(conn_str) as conn:
        with conn.cursor() as cursor:
            for i in range(int(os.environ[f'PG_QUERIES_PER_THREAD'])):
                print(f"querying for the {i} time")
                cursor.execute("""
                    select id
                    from whispers
                    order by timestamp desc
                    limit 10;
                """)
                results = cursor.fetchall()
                for item in results:
                    print(item[0])


if __name__ == '__main__':
    handler(None, None)
