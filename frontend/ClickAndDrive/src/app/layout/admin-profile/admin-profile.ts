import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { UserProfileInformation } from '../../services/models/user-profile-information';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-admin-profile',
  imports: [CommonModule],
  templateUrl: './admin-profile.html',
  styleUrls: ['./admin-profile.css'],
})
export class AdminProfile {

  adminButtons = [
    { label: 'Driver registration', route: 'driver-registration' },
    { label: 'Check current rides' },
    { label: 'Change prices' },
    { label: 'Ride history' },
    { label: 'Requests' },
    { label: 'Change information', route: 'change-information-page' },
    { label: 'Support' },
    { label: 'Reports' },
    { label: 'Notes' },
    { label: 'Log out', redText: true, logout: true }
  ];

  userName: string | null = null;
  user: UserProfileInformation | null = null;
  userId!: number;

  profileImageUrl: string = 'noProfilePic.svg';
  defaultProfileImage: string = 'noProfilePic.svg';
  selectedFile: File | null = null;
  isUploading: boolean = false;
  isDeleting: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private cdr: ChangeDetectorRef,
    private toastr: ToastrService
  ) {}

  
  ngOnInit(): void {
    const idFromToken = this.authService.getUserIdFromToken();

    if (!idFromToken) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return;
    }

    this.userId = idFromToken;

    this.userService.getUserProfileInfo(this.userId).subscribe({
      next: (res) => {
        this.user = res;
        this.userName = this.user.name + " " + this.user.surname;

        // Load profile image if exists
        if (res.profileImageUrl) {
          this.profileImageUrl = "http://localhost:8080" + res.profileImageUrl;
        }

        this.cdr.detectChanges();
      },
      error: () => {
        this.authService.logout();
        this.router.navigate(['/login']);
      }
    });
  }

  get leftButtons() {
    return this.adminButtons.slice(0, 5);
  }

  get rightButtons() {
    return this.adminButtons.slice(5);
  }

  onButtonClick(button: any) {
    if (button.logout) {
      this.authService.logout();
      return;
    }

    if (button.route) {
      this.router.navigate([button.route]);
    }
  }

  // Open file picker
  triggerFileInput() {
    document.getElementById('fileInput')?.click();
  }

  // When a user selects the file
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      this.toastr.info('Plase choose an image (JPG, PNG, GIF)', 'Info');
      return;
    }

    // Validate file size
    if (file.size > 5 * 1024 * 1024) {
      this.toastr.info('Image too large, max 5MB', 'Info');
      return;
    }

    this.selectedFile = file;
    this.uploadProfileImage();
  }

  // Image upload
  uploadProfileImage() {
    if (!this.selectedFile) return;

    this.isUploading = true;

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.userService.uploadProfileImage(this.userId, formData).subscribe({
      next: (response) => {
        this.profileImageUrl = 'http://localhost:8080' + response.profileImageUrl;
        this.isUploading = false;
        this.selectedFile = null;
        this.cdr.detectChanges();
        this.toastr.success(response.message, 'Success');
      },
      error: (err) => {
        console.error('Upload failed:', err);
        this.toastr.error('Error in setting up profile photo', 'Error');
        this.isUploading = false;
        this.selectedFile = null;
      }
    });
  }

  deleteProfileImage() {
    this.isDeleting = true;

    this.userService.deleteProfileImage(this.userId).subscribe({
      next: () => {
        this.profileImageUrl = this.defaultProfileImage;
        this.isDeleting = false;
        this.toastr.info('Profile image removed successfully', 'Info');
        this.cdr.detectChanges();
      },
      error: () => {
        this.toastr.error('Error removing profile image', 'Error');
        this.isDeleting = false;
      }
    });
  }

  hasCustomImage(): boolean {
    return this.profileImageUrl !== this.defaultProfileImage;
  }

  onImageError(event: any) {
    if (event.target.src !== this.defaultProfileImage) {
        event.target.src = this.defaultProfileImage;
    }
  }
}
