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

  searchByDate() {

    const start = this.fromDate ? new Date(this.fromDate).getTime() : -Infinity;
    const end = this.toDate
      ? new Date(this.toDate).setHours(23, 59, 59, 999)
      : Infinity;

    this.rides = this.allRides.filter(ride => {
      const rideDate = new Date(ride.startTime).getTime();
      return rideDate >= start && rideDate <= end;
    });

    this.sortBy();
  }

  clearFilter() {
    this.fromDate = '';
    this.toDate = '';
    this.rides = [...this.allRides];
    this.sortBy();
  }

  loadHistory() {
    if (!this.selectedUserId || !this.selectedRole) return;

    this.adminService
      .getRideHistory(this.selectedUserId, this.selectedRole, this.fromDate, this.toDate, this.sortField)
      .subscribe(data => {
        this.allRides = data;
        this.rides = [...data];
        this.sortBy(); // lokalno sortiranje ako želiš override
      });
  }

  sortBy() {
      this.rides.sort((a: any, b: any) => {
          switch(this.sortField) {
              case 'startTime': return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
              case 'endTime': return new Date(b.endTime).getTime() - new Date(a.endTime).getTime();
              case 'totalPrice': return b.totalPrice - a.totalPrice;
              case 'status': return b.status.localeCompare(a.status);
              default: return 0;
          }
      });
  }


  onUserChange() {
    const user = this.users.find(u => u.id == this.selectedUserId);
    this.selectedRole = user ? user.role : '';
  }
}
