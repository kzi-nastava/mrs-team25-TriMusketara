import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminRideHistory } from '../../services/models/admin-ride-history';

@Component({
  selector: 'app-admin-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-history.html',
  styleUrl: './admin-history.css',
})
export class AdminHistory implements OnInit {

  allRides: AdminRideHistory[] = [];
  rides: AdminRideHistory[] = [];

  fromDate: string = '';
  toDate: string = '';

  users: any[] = [];
  selectedUserId: number | null = null;
  selectedRole: string = '';

  sortField: string = 'startTime';

  constructor(
    private adminService: AdminService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.adminService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
      }
    });
  }

  loadHistory() {
    if (!this.selectedUserId || !this.selectedRole) return;

    this.adminService
      .getRideHistory(this.selectedUserId, this.selectedRole)
      .subscribe(data => {
        this.allRides = data;
        this.rides = [...data];
        this.sortBy();
      });
  }

  sortBy() {
    console.log(this.sortField);
    console.log(this.rides[0]);
    this.rides.sort((a: any, b: any) => {
      if (a[this.sortField] < b[this.sortField]) return 1;
      if (a[this.sortField] > b[this.sortField]) return -1;
      return 0;
    });
  }

  onUserChange() {
    const user = this.users.find(u => u.id == this.selectedUserId);
    this.selectedRole = user ? user.role : '';
  }
}
