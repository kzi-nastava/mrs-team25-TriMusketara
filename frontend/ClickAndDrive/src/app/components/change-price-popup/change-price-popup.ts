import { Component, OnInit, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { AdminPopupService } from '../../services/admin-popup.service';
import { VehiclePrice } from '../../services/models/vehicle-price';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-change-price-popup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-price-popup.html',
  styleUrls: ['./change-price-popup.css']
})
export class ChangePricePopup implements OnInit {
  prices: VehiclePrice = {
    standardBasePrice: 0,
    luxuryBasePrice: 0,
    vanBasePrice: 0,
    pricePerKm: 0
  };

  constructor(
    private adminService: AdminService, 
    public popupService: AdminPopupService,
    public toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.adminService.getPrices().subscribe({
      next: (res) => {
        this.prices = res;
        this.cdr.detectChanges(); // Force update to reflect new prices in the form
      },
      error: (err) => console.error("Greška pri ucitavanju cena:", err)
    });
  }

  save() {
    this.adminService.updatePrices(this.prices).subscribe({
      next: () => {
        this.toastr.success("Prices updated successfully!");
        this.popupService.closePrice();
      },
      error: (err) => {
        this.toastr.error("Failed to update prices.");
        console.error("Error updating prices:", err);
      }
    });
  }
}