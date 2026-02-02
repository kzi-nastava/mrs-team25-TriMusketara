import { Component, signal, ViewChild, ChangeDetectorRef } from '@angular/core';
import { MapViewComponent } from '../components/map-view/map-view';
import { RouterOutlet } from '@angular/router';
import { RidePopup } from '../shared/ride-popup';
import { RideOrdering } from '../layout/ride-ordering/ride-ordering';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
import { Location } from '../services/models/location';
import { SharedRideDataService } from '../services/shared-ride-data.service';


@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [MapViewComponent, RouterOutlet, RideOrdering, FormsModule],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css']
})
export class MainPageComponent {
  constructor(public ridePopup: RidePopup, public auth: AuthService, private sharedRideDataService: SharedRideDataService, private cdr: ChangeDetectorRef) {}

  @ViewChild('mapView') mapView!: MapViewComponent;

  originAddress = '';
  destinationAddress = '';

  prefilledOrigin?: string;
  prefilledDestination?: string;

  // Pending coordinates , we first have to see the status for when we order a ride
  pendingRouteCoordinates?: [number, number][];
  
  routeInfo?: {
    durationMinutes: number;
    distanceKm: number;
  }

  resolvedLocations?: {
    origin: Location;
    destination: Location;
    stops: Location[];
  };  

  showRideData = signal(false);

  async onShowRoute() {
    if (!this.originAddress || !this.destinationAddress) return;

    try {
      const originCoords = await this.geocodeAddress(this.originAddress);
      const destCoords = await this.geocodeAddress(this.destinationAddress);

      console.log(' Coordinates:', { originCoords, destCoords }); 

      if (this.mapView) {
        this.mapView.drawRouteAndCalculateETA(originCoords, destCoords);
      }

      this.showRideData.set(true);
      this.ridePopup.close();
    } catch (err) {
      console.error('Geocoding error:', err);
      alert('Address not found: ' + err);
    }
  }

  ngOnInit() {
    this.sharedRideDataService.prefilledData$.subscribe(data => {
      if (data) {
        this.prefilledOrigin = data.origin;
        this.prefilledDestination = data.destination;
        this.ridePopup.open();
      }
    });
  }

  onOverlayClick() {
    this.ridePopup.close();
  }

  // Converts text, example: Novi Sad, into map coordinates [lng, lat] using Mapbox Geocoding API
  private async geocodeAddress(address: string): Promise<[number, number]> {
    const token = 'pk.eyJ1IjoicmliaWNuaWtvbGEiLCJhIjoiY21qbTJvNHFlMmV6OTNncXhpOGNiaTVnayJ9.Bhzo0Euk2D923K3smmoVaQ';
    const bbox = [19.75, 45.20, 19.95, 45.30];
    const url = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(address)}.json` + `?access_token=${token}&limit=1&bbox=${bbox.join(',')}`;

    // const url = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(address)}.json?access_token=${token}&limit=1`;

    const response = await fetch(url);
    const data = await response.json();

    if (!data.features || !data.features.length) {
      throw new Error(address);
    }


    return data.features[0].center as [number, number];
  }

  // onEtaReceived(minutes: number) {
  //   this.etaMinutes = minutes;
  // }

  onRouteCalculated(info: {durationMinutes: number; distanceKm: number}) {
    this.routeInfo = info;
  }

  // Takes an ordered list of addresses and converts them into a list of coordinates
  private async geocodeAddressesSequentialy(addresses: string[]): Promise<[number, number][]> {
    const coordinates: [number, number][] = [];

    for (const address of addresses) {
      try {
        const coords = await this.geocodeAddress(address);
        coordinates.push(coords);
      } catch (err) {
        console.error('Failed to geocode:', address);
        throw err;
      }
    }

    return coordinates;
  }

  // Requested ride data from registered user form
  async onUserRideRequested(data: {
    origin: string,
    destination: string,
    stops: string[];
    }) {
      // Making an ordered list
      const allAddresses: string[] = [data.origin, ...data.stops, data.destination];

      try {
        // Geocode addresses
        const coordinates = await this.geocodeAddressesSequentialy(allAddresses);

        // Create Location objects to send to Ride object
        const locations: Location[] = allAddresses.map((address, i) => ({
          address,
          longitude: coordinates[i][0],
          latitude: coordinates[i][1]
        }));
        const origin = locations[0];
        const destination = locations[locations.length - 1];
        const stops = locations.slice(1, -1);

        this.pendingRouteCoordinates = coordinates;

        setTimeout(() => {
            this.resolvedLocations = {
              origin, 
              destination, 
              stops
            };            
            this.cdr.detectChanges();
          }, 0);

        // this.resolvedLocations = {
        //   origin,
        //   destination,
        //   stops
        // };

        // if (this.mapView) {
        //   this.mapView.drawRouteWithStops(coordinates);
        // }

        // this.showRideData.set(true);

      } catch (err) {
        alert('One of the addresses could not be found');
      }
  }

  onRideCreatedSuccessfully() {
    if (this.mapView && this.pendingRouteCoordinates) {
      this.mapView.drawRouteWithStops(this.pendingRouteCoordinates);
      this.showRideData.set(true);

      // Clear pending coordinates
      this.pendingRouteCoordinates = undefined;
    } else {
      console.error("Map view not found or coordinates missing");
    }
  }
}


