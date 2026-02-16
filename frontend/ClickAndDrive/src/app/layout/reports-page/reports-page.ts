import { Component, computed } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-reports-page',
  imports: [],
  templateUrl: './reports-page.html',
  styleUrl: './reports-page.css',
})
export class ReportsPage {

  constructor(private authService: AuthService) {}

  userRole = computed(() => this.authService.getRoleFromToken());

  ngOnInit() {
      console.log('User role:' + this.userRole);
  }

}
