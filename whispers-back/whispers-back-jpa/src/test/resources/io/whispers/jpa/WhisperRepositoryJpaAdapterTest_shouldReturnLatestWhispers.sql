insert into users (id, username) values ('27ff79e8-810a-4c73-af52-730555630dda', 'user');
insert into users (id, username) values ('00cb846c-331f-48e9-a214-786e6a53bb68', 'user2');
insert into topics (id, topic) values ('d3e795be-54f4-48ae-afdd-64717365b3ed', 'topic1');

insert into whispers (id, sender_id, text, timestamp, topic_id)
values ('d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef',
    '27ff79e8-810a-4c73-af52-730555630dda',
    'text1',
    '2000-01-01T10:30:00Z',
    'd3e795be-54f4-48ae-afdd-64717365b3ed');

insert into replies (id, sender_id, text, timestamp, whisper_id)
values ('d0873727-ba46-4ff7-a7f3-c33bae88058b',
    '00cb846c-331f-48e9-a214-786e6a53bb68',
    'replyText',
    '2000-01-01T10:31:00Z',
    'd1e99baf-6fde-44e2-88a7-a4a7d94ae3ef');

insert into whispers (id, sender_id, text, timestamp)
values ('47bd22fb-8b52-4c06-9c6d-ad8d5080ed7b',
    '27ff79e8-810a-4c73-af52-730555630dda',
    'text2',
    '2000-01-01T10:32:00Z');

insert into whispers (id, sender_id, text, timestamp)
values ('fd8ab3d4-201c-4941-aa08-0fa3b8b7976e',
    '27ff79e8-810a-4c73-af52-730555630dda',
    'text3',
    '2000-01-01T10:29:00Z');

insert into whispers (id, sender_id, text, timestamp)
values ('a70d21e6-a6c1-4b45-bf71-18aa15c06ee9',
    '00cb846c-331f-48e9-a214-786e6a53bb68',
    'text4',
    '2000-01-01T10:28:00Z');
