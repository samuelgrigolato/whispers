
export interface Whisper {
  id: string;
  sender: string;
  text: string;
  timestamp: string;
  topic?: string;
  replies: {
    sender: string;
    text: string;
    timestamp: string;
  }[];
}
