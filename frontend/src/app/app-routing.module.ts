import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { FolderListComponent } from './pages/folder-list/folder-list.component';
import { AuthGuard } from './guards/AuthGuard';
import { FileListComponent } from './pages/file-list/file-list.component';
import { HomeComponent } from './pages/public/home/home.component';
import { JobListComponent } from './pages/public/job-list/job-list.component';
import { JobDetailComponent } from './pages/public/job-detail/job-detail.component';
import { RecruiterDashboardComponent } from './pages/recruiter/recruiter-dashboard/recruiter-dashboard.component';
import { OfferManagementComponent } from './pages/recruiter/offer-management/offer-management.component';
import { CandidateKanbanComponent } from './pages/recruiter/candidate-kanban/candidate-kanban.component';

const routes: Routes = [
  // Public Routes
  { path: '', component: HomeComponent },
  { path: 'jobs', component: JobListComponent },
  { path: 'jobs/:id', component: JobDetailComponent },

  // Recruiter Routes
  { path: 'recruiter', component: RecruiterDashboardComponent },
  { path: 'recruiter/offers', component: OfferManagementComponent },
  { path: 'recruiter/candidates', component: CandidateKanbanComponent },

  // Existing Routes (Onboarding/Secure)
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: FolderListComponent, canActivate: [AuthGuard] },
  { path: 'folder/:id', component: FileListComponent, canActivate: [AuthGuard] },

  // Fallback
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
