import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteToggle } from './favorite-toggle';

describe('FavoriteToggle', () => {
  let component: FavoriteToggle;
  let fixture: ComponentFixture<FavoriteToggle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteToggle]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteToggle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
