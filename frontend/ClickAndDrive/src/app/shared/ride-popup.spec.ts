import { TestBed } from '@angular/core/testing';

import { RidePopup } from './ride-popup';

describe('RidePopup', () => {
  let service: RidePopup;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RidePopup);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
