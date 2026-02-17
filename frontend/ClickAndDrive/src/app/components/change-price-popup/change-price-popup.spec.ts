import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePricePopup } from './change-price-popup';

describe('ChangePricePopup', () => {
  let component: ChangePricePopup;
  let fixture: ComponentFixture<ChangePricePopup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePricePopup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangePricePopup);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
