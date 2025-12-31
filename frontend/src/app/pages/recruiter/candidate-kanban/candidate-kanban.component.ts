import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-candidate-kanban',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './candidate-kanban.component.html',
  styleUrls: ['./candidate-kanban.component.scss']
})
export class CandidateKanbanComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
