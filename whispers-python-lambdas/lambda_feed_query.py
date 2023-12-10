import os


STORAGE_TYPE = os.environ['STORAGE_TYPE'].upper() # 'DYNAMODB' or 'PG'


if STORAGE_TYPE == 'DYNAMODB':
    from lambda_feed_query_dynamodb import handler
elif STORAGE_TYPE == 'PG':
    from lambda_feed_query_pg import handler
else:
    raise Exception(f"Invalid STORAGE_TYPE: {STORAGE_TYPE}")


if __name__ == '__main__':
    handler(None, None)
