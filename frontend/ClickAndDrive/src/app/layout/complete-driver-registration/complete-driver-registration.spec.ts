import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompleteDriverRegistration } from './complete-driver-registration';

describe('CompleteDriverRegistration', () => {
  let component: CompleteDriverRegistration;
  let fixture: ComponentFixture<CompleteDriverRegistration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompleteDriverRegistration]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompleteDriverRegistration);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
