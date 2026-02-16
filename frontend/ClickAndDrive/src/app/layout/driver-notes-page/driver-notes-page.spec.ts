import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverNotesPage } from './driver-notes-page';

describe('DriverNotesPage', () => {
  let component: DriverNotesPage;
  let fixture: ComponentFixture<DriverNotesPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverNotesPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverNotesPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
