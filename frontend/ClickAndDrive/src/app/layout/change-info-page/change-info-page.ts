import { Component } from '@angular/core';

@Component({
  selector: 'app-change-info-page',
  imports: [],
  templateUrl: './change-info-page.html',
  styleUrl: './change-info-page.css',
})
export class ChangeInfoPage {
  fields = [
    {label: 'fname', text: 'First name:', type: 'text', placeholder: 'Jane'},
    {label: 'lname', text: 'Last name:', type: 'text', placeholder: 'Doe'},
    {label: 'email', text: 'Email:', type: 'text', placeholder: 'janedoe@gmail.com'},
    {label: 'address', text: 'Address:', type: 'text', placeholder: 'Las Noches, Hueco Mundo'},
    {label: 'mobile', text: 'Mobile:', type: 'text', placeholder: '123456789'},
  ];
}
