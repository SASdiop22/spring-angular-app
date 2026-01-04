import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { LoginComponent } from './pages/login/login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { FolderListComponent } from './pages/folder-list/folder-list.component';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { JwtInterceptor } from './interceptors/JwtInterceptor';
import { FileListComponent } from './pages/file-list/file-list.component';
import { RecruiterDashboardComponent } from './pages/recruiter-dashboard/recruiter-dashboard.component';
import { JobManagementComponent } from './pages/recruiter-dashboard/job-management/job-management.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatChipsModule } from '@angular/material/chips';
import { CandidatePortalComponent } from './pages/candidate-portal/candidate-portal.component';
import { ApplicationFormComponent } from './pages/candidate-portal/application-form/application-form.component';
import { OnboardingComponent } from './pages/candidate-portal/onboarding/onboarding.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    FolderListComponent,
    FileListComponent,
    RecruiterDashboardComponent,
    JobManagementComponent,
    CandidatePortalComponent,
    ApplicationFormComponent,
    OnboardingComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatInputModule,
    MatListModule,
    MatInputModule,
    MatListModule,
    MatSidenavModule,
    MatToolbarModule,
    MatChipsModule
  ],
  providers: [
    provideAnimationsAsync(),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
