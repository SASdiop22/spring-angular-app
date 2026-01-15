import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { LoginComponent } from "./pages/login/login.component"
import { FolderListComponent } from "./pages/folder-list/folder-list.component"
import { AuthGuard } from "./guards/AuthGuard"
import { FileListComponent } from "./pages/file-list/file-list.component"
import { HomeComponent } from "./pages/home/home.component"
import { JobOffersListComponent } from "./pages/job-offers-list/job-offers-list.component"
import { JobDetailComponent } from "./pages/job-detail/job-detail.component"

const routes: Routes = [
  { path: "", component: HomeComponent },
  { path: "login", component: LoginComponent },
  { path: "job-offers", component: JobOffersListComponent },
  { path: "job-offers/:id", component: JobDetailComponent },
  { path: "folders", component: FolderListComponent, canActivate: [AuthGuard] },
  { path: "folder/:id", component: FileListComponent, canActivate: [AuthGuard] },
  { path: "**", pathMatch: "full", redirectTo: "/" },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
