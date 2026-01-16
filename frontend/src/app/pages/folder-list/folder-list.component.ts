import { Component, OnInit } from '@angular/core';
import { Folder } from '../../models/Folder';
import { FolderService } from '../../services/folder.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-folder-list',
  templateUrl: './folder-list.component.html',
  styleUrl: './folder-list.component.scss'
})
export class FolderListComponent implements OnInit{

  folders:Folder[]=[];
  isCandidat = false;
  isRH = false;

  constructor(
    private folderService:FolderService,
    private router:Router,
    private authService: AuthService
  ){

  }
  ngOnInit(): void {
    this.isCandidat = this.authService.isCandidat();
    this.isRH = this.authService.isRH();
    this.folderService.getAllFolders().subscribe((folders:Folder[])=>{
      this.folders=folders;
    });
  }

  onClickFolder(folder:Folder){
    this.router.navigate(['/folder', folder.id]);
  }


}
