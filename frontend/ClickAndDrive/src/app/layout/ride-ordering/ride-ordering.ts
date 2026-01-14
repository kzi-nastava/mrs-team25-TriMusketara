import { Component } from '@angular/core';

@Component({
  selector: 'app-ride-ordering',
  imports: [],
  templateUrl: './ride-ordering.html',
  styleUrl: './ride-ordering.css',
})
export class RideOrdering {
  fields = [
    {label: 'origin', text: 'Origin:', type: 'text', placeholder: 'Zabalj', required: true},
    {label: 'destination', text: 'Destination:', type: 'text', placeholder: 'Novi Sad', required: true},
    {label: 'additional-stops', text: 'Additional stops:', type: 'text', placeholder: '...', required: true},
    {label: 'type', text: 'Vehicle type:', type: 'select', options: ['Luxury', 'Standard', 'Van'], required: true},
    {label: 'time', text: 'Set time:', type: 'time', placeholder: '', required: true},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: '', required: true},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: '', required: true},
  ];

  invalidFields: string[] = []; // List of invalid form fields

  onFinishOrder() {
    this.invalidFields = []; // empty the list

    // Check every field
    this.fields.forEach(field => {
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
