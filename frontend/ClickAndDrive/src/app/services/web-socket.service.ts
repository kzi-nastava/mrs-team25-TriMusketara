import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject, ReplaySubject, Subject } from 'rxjs';

import * as SockJS from 'sockjs-client';

// Ova linija je ključna - ona govori TypeScript-u da "zaboravi" 
// stroga pravila za ovu promenljivu
const SockJsFunc = (SockJS as any).default || SockJS; 

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompClient: Client;
  public rideUpdates$ = new Subject<any>();

  // Check is a connection is established
  public connected$ = new ReplaySubject<boolean>(1);
  public unreadCount$ = new BehaviorSubject<number>(0);

  // Notifications
  public notificationReceived$ = new ReplaySubject<any>(50);
  private subscriptions: Map<string, any> = new Map();

  constructor() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJsFunc('http://localhost:8080/ws'), // Backend URL
      debug: (msg) => console.log(msg),
      reconnectDelay: 5000,
    });

    this.stompClient.onConnect = () => {
      console.log('Connected to WebSocket');
      this.connected$.next(true);
      // Subskripcija na globalni kanal ili specifični za korisnika
      // Ako koristite Spring Security, možete koristiti /user/queue/ride-updates
      this.stompClient.subscribe('/topic/ride-events', (message: Message) => {
        this.rideUpdates$.next(JSON.parse(message.body));
      });
    };

    this.stompClient.onDisconnect = () => {
      console.log('Disconected from WebSocket');
    };
    
    this.stompClient.onStompError = (frame) => {
      console.error('Stomp error:', frame);
    };

    this.stompClient.activate();
  }

  // Opciona metoda ako želiš da se pretplatiš na specifičnu vožnju
  subscribeToRide(rideId: number) {
    this.stompClient.subscribe(`/topic/ride/${rideId}`, (message: Message) => {
      this.rideUpdates$.next(JSON.parse(message.body));
    });
  }

  subscribeToPassengerNotes(passengerId: number) {
    //console.log("subscribeToPassengerNotes called for passengerId:", passengerId);
    const topic = `/topic/passenger/${passengerId}/notes`;

    // Wait for established connection, then subscribe
    this.connected$.subscribe((val) => {
      //console.log("connected$ emmited:", val, "| connected:", this.stompClient.connected);

      if (this.subscriptions.has(topic)) {
        console.log("You are already subscribed to this channel:", topic);
        return;
      }

      //console.log("Making subscription:", topic);
      
      const subscription = this.stompClient.subscribe(topic, (message: Message) => {
        //console.log("Message arrived:", message.body);
        try {
          this.notificationReceived$.next(JSON.parse(message.body));
          this.unreadCount$.next(this.unreadCount$.value + 1);
        } catch (e) {
          this.notificationReceived$.next({ content: message.body, timestamp: 'Sad' });
        }
      });

      //console.log("Subscription created:", subscription);
      this.subscriptions.set(topic, subscription);
    });
  }
}
