import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { Subject } from 'rxjs';

import * as SockJS from 'sockjs-client';

// Ova linija je ključna - ona govori TypeScript-u da "zaboravi" 
// stroga pravila za ovu promenljivu
const SockJsFunc = (SockJS as any).default || SockJS; 

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompClient: Client;
  public rideUpdates$ = new Subject<any>();

  constructor() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJsFunc('http://localhost:8080/ws'), // Backend URL
      debug: (msg) => console.log(msg),
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      console.log('Connected to WebSocket');
      // Subskripcija na globalni kanal ili specifični za korisnika
      // Ako koristite Spring Security, možete koristiti /user/queue/ride-updates
      this.stompClient.subscribe('/topic/ride-events', (message: Message) => {
        this.rideUpdates$.next(JSON.parse(message.body));
      });
    };

    this.stompClient.activate();
  }

  // Opciona metoda ako želiš da se pretplatiš na specifičnu vožnju
  subscribeToRide(rideId: number) {
    this.stompClient.subscribe(`/topic/ride/${rideId}`, (message: Message) => {
      this.rideUpdates$.next(JSON.parse(message.body));
    });
  }
}