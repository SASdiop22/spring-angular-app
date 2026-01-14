import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  isAuthenticated$: Observable<boolean>;
  username: string | null = null;

  constructor(private authService: AuthService) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
  }

  ngOnInit(): void {
    this.isAuthenticated$.subscribe(isAuth => {
      if (isAuth) {
        this.username = this.authService.getCurrentUsername();
      } else {
        this.username = null;
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
