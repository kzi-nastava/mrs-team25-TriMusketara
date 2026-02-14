import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockReasonAlert } from './block-reason-alert';

describe('BlockReasonAlert', () => {
  let component: BlockReasonAlert;
  let fixture: ComponentFixture<BlockReasonAlert>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlockReasonAlert]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockReasonAlert);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
