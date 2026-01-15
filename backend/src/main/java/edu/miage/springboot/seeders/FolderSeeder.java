package edu.miage.springboot.seeders;

import edu.miage.springboot.dao.entities.FileEntity;
import edu.miage.springboot.dao.entities.FolderEntity;
import edu.miage.springboot.dao.repositories.FileRepository;
import edu.miage.springboot.dao.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FolderSeeder implements CommandLineRunner {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FileRepository fileRepository;

    @Override
    public void run(String... args) throws Exception {
        if (folderRepository.count() == 0) {
            FolderEntity folder1 = new FolderEntity();
            folder1.setName("Folder1");
            folder1 = folderRepository.save(folder1);

            FolderEntity folder2 = new FolderEntity();
            folder2.setName("Folder2");
            folder2 = folderRepository.save(folder2);

            // Créer des fichiers associés
            FileEntity file1 = new FileEntity();
            file1.setName("File1");
            file1.setFolder(folder1);
            fileRepository.save(file1);

            FileEntity file2 = new FileEntity();
            file2.setName("File2");
            file2.setFolder(folder2);
            fileRepository.save(file2);
        }
    }
}
