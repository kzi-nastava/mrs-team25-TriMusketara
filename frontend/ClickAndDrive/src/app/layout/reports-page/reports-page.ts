import { ChangeDetectorRef, Component, OnInit, computed } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserProfileInformation } from '../../services/models/user-profile-information';
import { ReportService } from '../../services/report.service';
import { AdminProfile } from '../admin-profile/admin-profile';
import { AdminService } from '../../services/admin.service';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartType } from 'chart.js';
import { ReportResponse } from '../../services/models/report-response';
import { ReportRequest } from '../../services/models/report-request';

@Component({
  selector: 'app-reports-page',
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './reports-page.html',
  styleUrl: './reports-page.css',
})
export class ReportsPage implements OnInit {

  constructor(private authService: AuthService, private reportService: ReportService, private adminService: AdminService, private cdr: ChangeDetectorRef) {}

  userRole = computed(() => {
    const role = this.authService.getRoleFromToken();
    return role;
  });

  isAdmin = computed(() => {
    const role = this.userRole();
    return role === 'Admin' || role === 'ROLE_ADMIN' || role === 'Administrator';
  });

  isEarnings(): boolean {
    if (this.userRole() === 'Driver') return true;
    if (this.userRole() === 'Passenger') return false;
    
    return this.selectedUserType === 'all-drivers' || this.selectedUserType === 'single-driver';
  }

  // Admin fields
  selectedUserType: string = '';
  selectedUserId: number | null = null;
  allDrivers: UserProfileInformation[] = [];
  allPassengers: UserProfileInformation[] = [];

  // Date fields
  dateFrom: string = '';
  dateTo: string = '';

  // Report data
  reportData: ReportResponse | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';

  // Chart data
  public ridesChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  };

  public kmChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  };

  public moneyChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  }

  public cumulativeRidesChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  }

  public cumulativeKmChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  }

  public cumulativeMoneyChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  }

  public chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
        labels: {
          color: '#e0e0e0'
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {color: '#aaaaaa'},
        grid: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      },
      x: {
        ticks: {color: '#aaaaaa'},
        grid: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      }
    }
  };

  public lineChartType: ChartType = 'line';

  ngOnInit() {
    console.log('User role: ' + this,this.userRole());

    // Set default dates
    const today = new Date();
    const thirtyDaysAgo = new Date(today);
    thirtyDaysAgo.setDate(today.getDate() - 30);

    this.dateTo = this.formatDateForInput(today);
    this.dateFrom = this.formatDateForInput(thirtyDaysAgo);

    // Load users for admin
    if (this.isAdmin()) {
      this.loadAllUsers();
    }
  }

  formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatDateForBackend(dateString: string): string {
    return dateString + 'T00:00:00';
  }

  loadAllUsers() {
    // Load both drivers and passengers
    this.adminService.getAllDrivers().subscribe({
      next: (drivers) => {
        this.allDrivers = drivers;
      },
      error: (err) => {
        console.error('Error loading drivers:', err);
      }
    });

    this.adminService.getAllPassengers().subscribe({
      next: (passengers) => {
        this.allPassengers = passengers;
      },
      error: (err) => {
        console.error('Error loading passengers:', err);
      }
    });
  }

  // Helper
  getUserList(): UserProfileInformation[] {
    if (this.selectedUserType === 'all-drivers' || this.selectedUserType === 'single-driver') {
      return this.allDrivers;
    } else if (this.selectedUserType === 'all-passengers' || this.selectedUserType === 'single-passenger') {
      return this.allPassengers;
    }
    return [];
  }

  // For regular users
  generateReport() {
    if (!this.validateDates()) return;

    this.isLoading = true;
    this.errorMessage = '';

    const request: ReportRequest = {
      dateFrom: this.formatDateForBackend(this.dateFrom),
      dateTo: this.formatDateForBackend(this.dateTo)
    };

    this.reportService.generateReport(request).subscribe({
      next: (response) => {
        this.reportData = response;
        this.updateCharts();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'Error generating report: ' + (err.error?.message || err.message);
        this.isLoading = false;
        console.error("Report error:", err);
      }
    });
  }

  // For admin
  generateAdminReport() {
    if (!this.validateDates()) return;

    if (!this.selectedUserType) {
      this.errorMessage = 'Please select user type';
      return;
    }

    if ((this.selectedUserType === 'single-driver' || this.selectedUserType === 'single-passenger') 
        && !this.selectedUserId) {
      this.errorMessage = 'Please select a user';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request: ReportRequest = {
      dateFrom: this.formatDateForBackend(this.dateFrom),
      dateTo: this.formatDateForBackend(this.dateTo)
    };

    if (this.selectedUserType === 'all-drivers') {
      request.userType = 'ALL_DRIVERS';
    } else if (this.selectedUserType === 'all-passengers') {
      request.userType = 'ALL_PASSENGERS';
    } else if (this.selectedUserType === 'single-driver' && this.selectedUserId) {
      request.userId = this.selectedUserId;
      request.userType = 'DRIVER';
    } else if (this.selectedUserType === 'single-passenger' && this.selectedUserId) {
      request.userId = this.selectedUserId;
      request.userType = 'PASSENGER';
    }

    this.reportService.generateReport(request).subscribe({
      next: (response) => {
        this.reportData = response;
        this.updateCharts();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'Error generating report: ' + (err.error?.message || err.message);
        this.isLoading = false;
        console.error('Report error:', err);
      }
    });
  }

  validateDates(): boolean {
    if (!this.dateFrom || !this.dateTo) {
      this.errorMessage = 'Please select both dates';
      return false;
    }

    if (new Date(this.dateFrom) > new Date(this.dateTo)) {
      this.errorMessage = 'Date "from" cannot be after date "to"';
      return false;
    }

    return true;
  }

  updateCharts() {
    if (!this.reportData) return;

    const labels = this.reportData.dailyStats.map(stat => {
      // Format nicely
      const date = new Date(stat.date);
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    });

    const ridesData = this.reportData.dailyStats.map(stat => stat.numberOfRides);
    const kmData = this.reportData.dailyStats.map(stat => stat.totalKilometers);
    const moneyData = this.reportData.dailyStats.map(stat => stat.totalMoney);

    const cumulativeRidesData = this.reportData.dailyStats.map(stat => stat.cumulativeRides);
    const cumulativeKmData = this.reportData.dailyStats.map(stat => stat.cumulativeKilometers);
    const cumulativeMoneyData = this.reportData.dailyStats.map(stat => stat.cumulativeMoney);

    // Rides chart
    this.ridesChartData = {
      labels: labels,
      datasets: [
        {
          data: ridesData,
          label: 'Number of Rides',
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.2)',
          tension: 0.4,
          fill: true
        }
      ]
    };

    // Kilometers chart
    this.kmChartData = {
      labels: labels,
      datasets: [
        {
          data: kmData,
          label: 'Kilometers',
          borderColor: '#10b981',
          backgroundColor: 'rgba(16, 185, 129, 0.2)',
          tension: 0.4,
          fill: true
        }
      ]
    };

    // Money chart
    this.moneyChartData = {
      labels: labels,
      datasets: [
        {
          data: moneyData,
          label: this.getMoneyLabel(),
          borderColor: '#f59e0b',
          backgroundColor: 'rgba(245, 158, 11, 0.2)',
          tension: 0.4,
          fill: true
        }
      ]
    };

    // Cumulative charts
    this.cumulativeRidesChartData = {
      labels: labels,
      datasets: [
        {
          data: cumulativeRidesData,
          label: 'Cumulative Rides',
          borderColor: '#8b5cf6',
          backgroundColor: 'rgba(139, 92, 246, 0.2)',
          tension: 0.4,
          fill: true
        }
      ]
    };
  
    this.cumulativeKmChartData = {
      labels: labels,
      datasets: [
        {
          data: cumulativeKmData,
          label: 'Cumulative Kilometers',
          borderColor: '#ec4899',
          backgroundColor: 'rgba(236, 72, 153, 0.2)',
          tension: 0.4,
          fill: true
        }
      ]
    };
  
    this.cumulativeMoneyChartData = {
      labels: labels,
      datasets: [
        {
          data: cumulativeMoneyData,
          label: 'Cumulative ' + (this.isEarnings() ? 'Earnings' : 'Spending'),
          borderColor: '#14b8a6',
          backgroundColor: 'rgba(20, 184, 166, 0.2)',
          tension: 0.4,
          fill: true
        }
      ]
    };
  }

  getMoneyLabel(): string {
    if (this.userRole() === 'Driver' || 
        this.selectedUserType === 'all-drivers' || 
        this.selectedUserType === 'single-driver') {
      return 'Money Earned (RSD)';
    }
    return 'Money Spent (RSD)';
  }

  onUserTypeChange() {
    // Reset user selection when type changes
    this.selectedUserId = null;
  }
}
