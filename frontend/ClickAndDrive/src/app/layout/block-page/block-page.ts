import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { UserProfileInformation } from '../../services/models/user-profile-information';
import { AdminService } from '../../services/admin.service';
import { BlockReasonInput } from '../../components/block-reason-input/block-reason-input';

@Component({
  selector: 'app-block-page',
  imports: [CommonModule, BlockReasonInput],
  templateUrl: './block-page.html',
  styleUrl: './block-page.css',
})
export class BlockPage implements OnInit {

  // List that is displayed
  users: UserProfileInformation[] = [];

  currentView: 'drivers' | 'passengers' = 'drivers';

  @ViewChild(BlockReasonInput) noteInput!: BlockReasonInput;
  selectedUserForNote: UserProfileInformation | null = null;

  constructor(private adminService: AdminService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadDrivers();
  }

  // Show drivers
  showDrivers() {
    this.currentView = 'drivers';
    this.loadDrivers();
  }

  // Show passengers
  showPassengers() {
    this.currentView = 'passengers';
    this.loadPassengers();
  }

  loadDrivers() {
    this.adminService.getAllDrivers().subscribe({
      next: (data) => {
        this.users = data,
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  loadPassengers() {
    this.adminService.getAllPassengers().subscribe({
      next: (data) => {
        this.users = data,
        this.cdr.detectChanges();
      },
      error: (err) => console.log(err)
    });
  }

  // Block/unblock button
  toggleBlockStatus(user: UserProfileInformation) {
    if (user.blocked) {
      if (confirm(`Are you sure you want to unblock ${user.name}`)) {
        this.adminService.unblockUser(user.id).subscribe(updatedUser => {
          this.updateUserInList(updatedUser);
        });
      }
    } else {
        if(confirm(`Block ${user.name}?`)) {
          this.adminService.blockUser(user.id, "").subscribe(updatedUser => {
              this.updateUserInList(updatedUser);
          });
        } 
      }
  }
  

  // Leave a note
  openNoteModal(user: UserProfileInformation) {
    this.selectedUserForNote = user;
    this.noteInput.open(user.blockReason || '');
  }

  onNoteSaved(note: string) {
    if (this.selectedUserForNote) {
      this.adminService.leaveNote(this.selectedUserForNote.id, note).subscribe(updatedUser => {
          this.updateUserInList(updatedUser);
          this.selectedUserForNote = null;
      });
    }
  }

  private updateUserInList(updatedUser: UserProfileInformation) {
    const index = this.users.findIndex(u => u.id === updatedUser.id);
      if (index !== -1) {
          const newUsers = [...this.users];
          newUsers[index] = updatedUser;
          this.users = newUsers;
          this.cdr.detectChanges();
      }
  }
}
