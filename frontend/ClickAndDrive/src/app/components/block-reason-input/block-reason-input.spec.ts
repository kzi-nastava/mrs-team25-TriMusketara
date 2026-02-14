import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockReasonInput } from './block-reason-input';

describe('BlockReasonInput', () => {
  let component: BlockReasonInput;
  let fixture: ComponentFixture<BlockReasonInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlockReasonInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockReasonInput);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
