import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateKanbanComponent } from './candidate-kanban.component';

describe('CandidateKanbanComponent', () => {
  let component: CandidateKanbanComponent;
  let fixture: ComponentFixture<CandidateKanbanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CandidateKanbanComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CandidateKanbanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
