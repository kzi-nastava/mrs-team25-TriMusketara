import { Component } from '@angular/core';

@Component({
  selector: 'app-ride-ordering',
  imports: [],
  templateUrl: './ride-ordering.html',
  styleUrl: './ride-ordering.css',
})
export class RideOrdering {
  fields = [
    {label: 'origin', text: 'Origin:', type: 'text', placeholder: 'Zabalj'},
    {label: 'destination', text: 'Destination:', type: 'text', placeholder: 'Novi Sad'},
    {label: 'additional-stops', text: 'Additional stops:', type: 'text', placeholder: '...'},
    {label: 'type', text: 'Vehicle type:', type: 'select', options: ['Luxury', 'Standard', 'Van']},
    {label: 'time', text: 'Set time:', type: 'time', placeholder: ''},
    {label: 'baby-friendly', text: 'Baby friendly:', type: 'checkbox', placeholder: ''},
    {label: 'pet-friendly', text: 'Pet friendly:', type: 'checkbox', placeholder: ''},
  ];
}
