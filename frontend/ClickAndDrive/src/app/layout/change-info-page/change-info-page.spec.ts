import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeInfoPage } from './change-info-page';

describe('ChangeInfoPage', () => {
  let component: ChangeInfoPage;
  let fixture: ComponentFixture<ChangeInfoPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeInfoPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeInfoPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
