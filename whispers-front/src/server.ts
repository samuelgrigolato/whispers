import { createServer } from 'miragejs';
import { v4 as uuidv4 } from 'uuid';

export function startFakeApiServer() {
  const whispers = [
    {
      id: 'bd51a65e-fe36-4af3-8443-dc854bc01d1d',
      sender: 'samuel',
      text: "Good morning, everyone! Hope you're all having a fantastic day! 😊 #positivity #goodvibes",
      timestamp: '2023-11-24T15:47:00Z',
      topic: 'good morning',
      replies: [
        {
          id: '1914eb5e-7b50-48e0-9506-45cda42c18e3',
          sender: 'mari',
          text: 'Good morning! Definitely feeling the positive vibes today. Hope your day is just as amazing! ☀️ #GoodVibesOnly',
          timestamp: '2023-11-24T15:49:00Z',
        },
        {
          id: '5285b8ca-fbab-4eb2-9c8e-b9b2d56c089c',
          sender: 'john',
          text: 'Morning! Thanks for the cheerful start to the day. Wishing you a fantastic day as well! 😊🌟 #positiveenergy #morningmotivation',
          timestamp: '2023-11-24T15:51:00Z',
        }
      ],
    },
    {
      id: '3e02dc6a-441b-4195-bdf3-980a46f03829',
      sender: 'mari',
      text: "Just finished a great book! Any recommendations for what I should read next? 📚 #booklovers #readingcommunity",
      timestamp: '2023-11-23T15:47:00Z',
      replies: [],
    },
    {
      id: 'c24cee2c-6c0e-4d04-858f-a91da0a5a3ab',
      sender: 'john',
      text: "Just tried a new recipe and it turned out great! Anyone else love cooking? 🍳🥘 #foodie #homemademeals",
      timestamp: '2023-11-15T15:47:00Z',
      replies: [],
      topic: 'AI',
    }
  ];

  return createServer({
    routes() {
      this.namespace = 'api';

      this.get('/whispers', (_: any, request: any) => {
        const topic = request.queryParams.topic;
        return whispers.filter(x => !topic || x.topic === topic);
      });

      this.get('/whispers/mine', (_: any, request: any) => {
        const me = request.requestHeaders['Authorization'].substring('Bearer '.length);
        return whispers.filter(x => x.sender === me);
      });

      this.post('/whispers', (_: any, request: any) => {
        const payload = JSON.parse(request.requestBody);
        const sender = request.requestHeaders['Authorization'].substring('Bearer '.length);
        return {
          id: uuidv4(),
          sender,
          text: payload.text,
          timestamp: new Date().toISOString(),
          replies: [],
        };
      });

      this.get('/topics/trending', () => {
        return [
          {
            topic: 'good morning',
            whispers: 740000,
          },
          {
            topic: 'AI',
            whispers: 600000,
          },
          {
            topic: 'pizza',
            whispers: 300,
          },
        ];
      });
    },
  });
};
