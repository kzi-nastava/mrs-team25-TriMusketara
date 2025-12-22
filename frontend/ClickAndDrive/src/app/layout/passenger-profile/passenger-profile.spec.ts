import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerProfile } from './passenger-profile';

describe('PassengerProfile', () => {
  let component: PassengerProfile;
  let fixture: ComponentFixture<PassengerProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerProfile);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
