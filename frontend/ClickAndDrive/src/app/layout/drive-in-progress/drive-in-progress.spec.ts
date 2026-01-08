import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriveInProgress } from './drive-in-progress';

describe('DriveInProgress', () => {
  let component: DriveInProgress;
  let fixture: ComponentFixture<DriveInProgress>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriveInProgress]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriveInProgress);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
