package edu.miage.springboot.seeders;

import edu.miage.springboot.dao.entities.FolderEntity;
import edu.miage.springboot.dao.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FolderSeeder implements CommandLineRunner {

    @Autowired
    private FolderRepository folderRepository;
    @Override
    public void run(String... args) throws Exception {

        FolderEntity folder=new FolderEntity();
        folder.setName("Folder1");
        folderRepository.save(folder);

    }
}
