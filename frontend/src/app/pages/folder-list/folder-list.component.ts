import { Component, OnInit } from '@angular/core';
import { Folder } from '../../models/Folder';
import { FolderService } from '../../services/folder.service';

@Component({
  selector: 'app-folder-list',
  templateUrl: './folder-list.component.html',
  styleUrl: './folder-list.component.scss'
})
export class FolderListComponent implements OnInit{

  folders:Folder[]=[];

  constructor(private folderService:FolderService){

  }
  ngOnInit(): void {
    this.folderService.getAllFolders().subscribe((folders:Folder[])=>{
      this.folders=folders;
    });
  }


}
