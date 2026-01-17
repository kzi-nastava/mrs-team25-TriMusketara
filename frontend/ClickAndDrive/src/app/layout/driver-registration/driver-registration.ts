import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProfileSidebarService } from '../../services/profile-sidebar.service';

@Component({
  selector: 'app-driver-registration',
  imports: [],
  templateUrl: './driver-registration.html',
  styleUrl: './driver-registration.css',
})
export class DriverRegistration {
  driverFields = [
    {label: 'fname', text: 'First name:', type: 'text', placeholder: 'Jane', required: true},
    {label: 'lname', text: 'Last name:', type: 'text', placeholder: 'Doe', required: true},
    {label: 'email', text: 'Email:', type: 'email', placeholder: 'janedoe@gmail.com', required: true},
    {label: 'password', text: 'Password:', type: 'password', placeholder: '*************', required: true},
    {label: 'address', text: 'Address:', type: 'text', placeholder: 'Las Noches, Hueco Mundo', required: true},
    {label: 'mobile', text: 'Mobile:', type: 'text', placeholder: '123456789', required: true},
  ];

  vehicleFields = [
    {label: 'model', text: 'Model:', type: 'text', placeholder: 'Model', required: true},
    {label: 'type', text: 'Type:', type: 'select', options: ['Luxury', 'Standard', 'Van'], required: true},
    {label: 'licence', text: 'Plate number:', type: 'text', placeholder: 'NS319KK', required: true},
    {label: 'seats', text: 'Num. of seats:', type: 'number', placeholder: '', min: 4, max: 12, required: true},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: '', required: true},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: '', required: true},
  ];

  public constructor(private router: Router, public profileSidebar: ProfileSidebarService) {}

  invalidFields: string[] = []; // List of invalid form fields

  showVehicleInfo = false; // This flag is used for page responsiveness 768px and 480px 

  goNext() {
    this.showVehicleInfo = true; // When the 'next' button is pressed in the form, continue to register the drivers vehicle
  }

  goBack() {
    this.showVehicleInfo = false; // When the 'back' button is pressed in the form, return to driver information form
  }


  // When canceling driver registration go to main page , but also open the profile sidebar
  closeRegistrationForm() {
    this.router.navigate(['/']).then(() => {
      this.profileSidebar.open();
    })
  }

  get allFields() {
    return [...this.driverFields, ...this.vehicleFields];
  }

  // Take form inputs and register a new driver
  registerDriver() {
    this.invalidFields = []; // empty the list

    // Check every field
    // For now we are only checking to see if the fields are empty, so we can apply a border style
    this.allFields.forEach(field => {
      if (field.required) {
        const element = document.getElementById(field.label) as HTMLInputElement | HTMLSelectElement;

        if (field.type === 'checkbox') {
          const checkbox = element as HTMLInputElement;
          if (!checkbox.checked) {
            this.invalidFields.push(field.label);
          }
        } else {
          if (!element.value || element.value.trim() === '') {
            this.invalidFields.push(field.label);
          }
        }
      }
    });
    // .....
  }

  isFieldInvalid(label: string): boolean {
    return this.invalidFields.includes(label);
  }

}
