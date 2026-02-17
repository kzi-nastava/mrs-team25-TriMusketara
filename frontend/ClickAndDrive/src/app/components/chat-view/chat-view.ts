import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { AdminPopupService } from '../../services/admin-popup.service';
import { ChatMessage } from '../../services/models/chat-message';
import { ChatService } from '../../services/chat.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-chat-view',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-view.html',
  styleUrls: ['./chat-view.css']
})
export class ChatViewComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  messages: ChatMessage[] = [];
  chatUsers: string[] = []; // List of users who have chatted with admin
  selectedUserEmail: string | null = null;
  newMessage: string = '';
  adminEmail: string = 'admin@demo.com'; 
  
  private pollInterval: any;

  constructor(
    public auth: AuthService,
    public popupService: AdminPopupService,
    private chatService: ChatService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.auth.userType() === 'admin') {
      this.loadChatUsers();
      
      // Admin osvežava listu korisnika periodično
      this.pollInterval = setInterval(() => this.loadChatUsers(), 5000);
    } else {
      // Običan korisnik/vozač odmah ide na čet sa adminom
      this.selectedUserEmail = this.adminEmail;
      this.loadHistory();

      // Korisnik osvežava poruke periodično
      this.pollInterval = setInterval(() => this.loadHistory(), 3000);
    }
  }

  ngOnDestroy(): void {
    if (this.pollInterval) {
      clearInterval(this.pollInterval);
    }
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }

  loadChatUsers() {
    this.chatService.getChatUsers(this.adminEmail).subscribe({
      next: (users) => {this.chatUsers = users; this.cdr.detectChanges();},
      error: (err) => console.error("403 or Auth error:", err)
    });
  }

  loadHistory() {
    if (!this.selectedUserEmail) return;
    const myEmail = this.auth.userName(); // Proveri da li userName() vraća email!
    
    this.chatService.getHistory(this.selectedUserEmail, myEmail).subscribe({
      next: (msgs) => {
        this.messages = msgs;
        this.markAsSeen();
        this.cdr.detectChanges();
      }
    });
  }

  send() {
    if (!this.newMessage.trim() || !this.selectedUserEmail) return;

    const dto: ChatMessage = {
      message: this.newMessage,
      senderEmail: this.auth.userName(),
      receiverEmail: this.selectedUserEmail,
      sentAt: new Date().toISOString(),
      seen: false
    };

    this.chatService.sendMessage(dto).subscribe(() => {
      this.messages.push(dto);
      this.newMessage = '';
      this.cdr.detectChanges();
    });
  }

  markAsSeen() {
    const myEmail = this.auth.userName();
    this.chatService.markAsSeen(myEmail, this.selectedUserEmail!).subscribe();
  }

  selectUser(email: string) {
    this.selectedUserEmail = email;
    this.loadHistory();
    // Prebacujemo polling na poruke konkretnog usera
    if (this.pollInterval) clearInterval(this.pollInterval);
    this.pollInterval = setInterval(() => this.loadHistory(), 3000);
  }

  close() {
    this.popupService.closeChat();
  }
}