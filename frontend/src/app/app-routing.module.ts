import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { FolderListComponent } from './pages/folder-list/folder-list.component';
import { AuthGuard } from './guards/AuthGuard';
import { FileListComponent } from './pages/file-list/file-list.component';
import { RecruiterDashboardComponent } from './pages/recruiter-dashboard/recruiter-dashboard.component';
import { JobManagementComponent } from './pages/recruiter-dashboard/job-management/job-management.component';
import { CandidatePortalComponent } from './pages/candidate-portal/candidate-portal.component';
import { ApplicationFormComponent } from './pages/candidate-portal/application-form/application-form.component';
import { OnboardingComponent } from './pages/candidate-portal/onboarding/onboarding.component';

const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "", redirectTo: "jobs", pathMatch: "full" },
  { path: "folder/:id", component: FileListComponent, canActivate: [AuthGuard], },
  {
    path: "recruiter",
    component: RecruiterDashboardComponent,
    // canActivate: [AuthGuard], // Uncomment when auth is ready
    children: [
      { path: '', redirectTo: 'jobs', pathMatch: 'full' },
      { path: 'jobs', component: JobManagementComponent },
      // { path: 'candidates', component: CandidateReviewComponent } // To be implemented
    ]
  },
  { path: "jobs", component: CandidatePortalComponent },
  { path: "jobs/:id/apply", component: ApplicationFormComponent },
  { path: "onboarding", component: OnboardingComponent },
  { path: "**", pathMatch: 'full', redirectTo: "/" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
