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
import { HeaderComponent } from './core/layout/header/header.component';
import { FooterComponent } from './core/layout/footer/footer.component';
import { HomeComponent } from './pages/public/home/home.component';
import { JobListComponent } from './pages/public/job-list/job-list.component';
import { JobDetailComponent } from './pages/public/job-detail/job-detail.component';
import { RecruiterDashboardComponent } from './pages/recruiter/recruiter-dashboard/recruiter-dashboard.component';
import { OfferManagementComponent } from './pages/recruiter/offer-management/offer-management.component';
import { CandidateKanbanComponent } from './pages/recruiter/candidate-kanban/candidate-kanban.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    FolderListComponent,
    FileListComponent
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
    MatListModule,
    // Standalone Components
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    JobListComponent,
    JobDetailComponent,
    RecruiterDashboardComponent,
    OfferManagementComponent,
    CandidateKanbanComponent
  ],
  providers: [
    provideAnimationsAsync(),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
