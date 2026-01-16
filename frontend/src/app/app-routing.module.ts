import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { LoginComponent } from "./pages/login/login.component"
import { FolderListComponent } from "./pages/folder-list/folder-list.component"
import { AuthGuard } from "./guards/AuthGuard"
import { RHGuard, AdminGuard } from "./guards/RoleGuard"
import { FileListComponent } from "./pages/file-list/file-list.component"
import { HomeComponent } from "./pages/home/home.component"
import { JobOffersListComponent } from "./pages/job-offers-list/job-offers-list.component"
import { JobDetailComponent } from "./pages/job-detail/job-detail.component"
import { AddJobOfferComponent } from "./pages/add-job-offer/add-job-offer.component"
import { CandidatesComponent } from "./pages/candidates/candidates.component"
import { DashboardComponent } from "./pages/dashboard/dashboard.component"
import { JobCandidatesComponent } from "./pages/job-candidates/job-candidates.component"
import { UserProfileComponent } from "./pages/user-profile/user-profile.component"
import { UserApplicationsComponent } from "./pages/user-applications/user-applications.component"
import { AdminDashboardComponent } from "./pages/admin-dashboard/admin-dashboard.component"

const routes: Routes = [
  { path: "", component: HomeComponent },
  { path: "login", component: LoginComponent },
  { path: "admin", component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: "job-offers", component: JobOffersListComponent },
  { path: "job-offers/:id", component: JobDetailComponent },
  { path: "job-offers/:id/edit", component: AddJobOfferComponent, canActivate: [RHGuard] },
  { path: "job-offers/:id/candidates", component: JobCandidatesComponent, canActivate: [AuthGuard] },
  { path: "candidates", component: CandidatesComponent },
  { path: "add-job-offer", component: AddJobOfferComponent, canActivate: [RHGuard] },
  { path: "dashboard", component: DashboardComponent, canActivate: [AuthGuard] },
  { path: "user/profile", component: UserProfileComponent, canActivate: [AuthGuard] },
  { path: "user/applications", component: UserApplicationsComponent, canActivate: [AuthGuard] },
  { path: "folders", component: FolderListComponent, canActivate: [AuthGuard] },
  { path: "folder/:id", component: FileListComponent, canActivate: [AuthGuard] },
  { path: "**", pathMatch: "full", redirectTo: "/" },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
