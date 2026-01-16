import { NgModule } from "@angular/core"
import { BrowserModule } from "@angular/platform-browser"
import { CommonModule } from "@angular/common"
import { AppRoutingModule } from "./app-routing.module"
import { AppComponent } from "./app.component"
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http"
import { provideAnimationsAsync } from "@angular/platform-browser/animations/async"
import { MatCardModule } from "@angular/material/card"
import { MatFormFieldModule } from "@angular/material/form-field"
import { LoginComponent } from "./pages/login/login.component"
import { ReactiveFormsModule, FormsModule } from "@angular/forms"
import { MatButtonModule } from "@angular/material/button"
import { MatInputModule } from "@angular/material/input"
import { FolderListComponent } from "./pages/folder-list/folder-list.component"
import { MatListModule } from "@angular/material/list"
import { MatIconModule } from "@angular/material/icon"
import { MatCommonModule } from "@angular/material/core"
import { JwtInterceptor } from "./interceptors/JwtInterceptor"
import { FileListComponent } from "./pages/file-list/file-list.component"
import { HomeComponent } from "./pages/home/home.component"
import { JobOffersListComponent } from "./pages/job-offers-list/job-offers-list.component"
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner"
import { MatMenuModule } from "@angular/material/menu"
import { MatSelectModule } from "@angular/material/select"
import { JobDetailComponent } from "./pages/job-detail/job-detail.component"
import { AddJobOfferComponent } from "./pages/add-job-offer/add-job-offer.component"
import { CandidatesComponent } from "./pages/candidates/candidates.component"
import { DashboardComponent } from "./pages/dashboard/dashboard.component"
import { HeaderComponent } from "./components/header/header.component"
import { JobCandidatesComponent } from "./pages/job-candidates/job-candidates.component"
import { UserProfileComponent } from "./pages/user-profile/user-profile.component"
import { UserApplicationsComponent } from "./pages/user-applications/user-applications.component"

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    FolderListComponent,
    FileListComponent,
    HomeComponent,
    JobOffersListComponent,
    JobDetailComponent,
    AddJobOfferComponent,
    CandidatesComponent,
    DashboardComponent,
    HeaderComponent,
    JobCandidatesComponent,
    UserProfileComponent,
    UserApplicationsComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    MatIconModule,
    MatCommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatSelectModule,
  ],
  providers: [provideAnimationsAsync(), { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }],
  bootstrap: [AppComponent],
})
export class AppModule { }

