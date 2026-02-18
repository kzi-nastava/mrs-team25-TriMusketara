import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatMessage } from './models/chat-message';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) {}

  getHistory(userEmail: string, adminEmail: string): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/${userEmail}/${adminEmail}`);
  }

  sendMessage(dto: ChatMessage): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${this.apiUrl}/send`, dto);
  }

  getChatUsers(adminEmail: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/admin/users/${adminEmail}`);
  }

  markAsSeen(receiverEmail: string, senderEmail: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/seen/${receiverEmail}/${senderEmail}`, {});
  }
}