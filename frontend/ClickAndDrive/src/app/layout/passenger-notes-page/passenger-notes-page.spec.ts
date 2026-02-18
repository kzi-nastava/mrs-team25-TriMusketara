import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerNotesPage } from './passenger-notes-page';

describe('PassengerNotesPage', () => {
  let component: PassengerNotesPage;
  let fixture: ComponentFixture<PassengerNotesPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerNotesPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerNotesPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
