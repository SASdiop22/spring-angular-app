import { Injectable } from '@angular/core';
import { Folder } from '../models/Folder';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FolderService {

  constructor(private http:HttpClient) { }

  getAllFolders():Observable<Folder[]> {
    const url="http://localhost:8080/api/folders";
    return this.http.get<Folder[]>(url);
  }
}
