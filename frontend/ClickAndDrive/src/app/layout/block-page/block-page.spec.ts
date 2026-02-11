import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockPage } from './block-page';

describe('BlockPage', () => {
  let component: BlockPage;
  let fixture: ComponentFixture<BlockPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlockPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
