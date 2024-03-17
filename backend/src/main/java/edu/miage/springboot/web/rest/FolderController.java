package edu.miage.springboot.web.rest;

import edu.miage.springboot.services.interfaces.FolderService;
import edu.miage.springboot.web.dtos.FolderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public ResponseEntity<List<FolderDTO>> getAll() {
        return ResponseEntity.ok(folderService.getAll());
    }
}
