import { Component } from '@angular/core';

@Component({
  selector: 'app-driver-registration',
  imports: [],
  templateUrl: './driver-registration.html',
  styleUrl: './driver-registration.css',
})
export class DriverRegistration {
  driverFields = [
    {label: 'fname', text: 'First name:', type: 'text', placeholder: 'Jane'},
    {label: 'lname', text: 'Last name:', type: 'text', placeholder: 'Doe'},
    {label: 'email', text: 'Email:', type: 'email', placeholder: 'janedoe@gmail.com'},
    {label: 'password', text: 'Password:', type: 'password', placeholder: '*************'},
    {label: 'address', text: 'Address:', type: 'text', placeholder: 'Las Noches, Hueco Mundo'},
    {label: 'mobile', text: 'Mobile:', type: 'text', placeholder: '123456789'},
  ];

  vehicleFields = [
    {label: 'model', text: 'Model:', type: 'text', placeholder: 'Model'},
    {label: 'type', text: 'Type:', type: 'select', options: ['Luxury', 'Standard', 'Van']},
    {label: 'licence', text: 'Plate number:', type: 'text', placeholder: 'NS319KK'},
    {label: 'seats', text: 'Num. of seats:', type: 'number', placeholder: '', min: 4, max: 12},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: ''},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: ''},
  ];
}
