export interface ChatMessage {
  message: string;
  senderEmail: string;
  receiverEmail: string;
  sentAt: string; // ISO format date string
  seen: boolean;
}