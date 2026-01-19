import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideRating } from './ride-rating';

describe('RideRating', () => {
  let component: RideRating;
  let fixture: ComponentFixture<RideRating>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideRating]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideRating);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
