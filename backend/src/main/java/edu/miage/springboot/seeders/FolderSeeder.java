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

            // === DOSSIER: Documentation ===
            FolderEntity documentation = new FolderEntity();
            documentation.setName("Documentation");
            documentation = folderRepository.save(documentation);

            FileEntity doc1 = new FileEntity();
            doc1.setName("Guide_Utilisateur.pdf");
            doc1.setFolder(documentation);
            fileRepository.save(doc1);

            FileEntity doc2 = new FileEntity();
            doc2.setName("Politique_Confidentialite.docx");
            doc2.setFolder(documentation);
            fileRepository.save(doc2);

            FileEntity doc3 = new FileEntity();
            doc3.setName("Contrat_Travail.pdf");
            doc3.setFolder(documentation);
            fileRepository.save(doc3);

            // === DOSSIER: Ressources Humaines ===
            FolderEntity rh = new FolderEntity();
            rh.setName("Ressources Humaines");
            rh = folderRepository.save(rh);

            FileEntity rh1 = new FileEntity();
            rh1.setName("Processus_Recrutement.pdf");
            rh1.setFolder(rh);
            fileRepository.save(rh1);

            FileEntity rh2 = new FileEntity();
            rh2.setName("Grille_Salaires_2026.xlsx");
            rh2.setFolder(rh);
            fileRepository.save(rh2);

            FileEntity rh3 = new FileEntity();
            rh3.setName("Evaluation_Competences.docx");
            rh3.setFolder(rh);
            fileRepository.save(rh3);

            FileEntity rh4 = new FileEntity();
            rh4.setName("Plan_Formation_2026.pdf");
            rh4.setFolder(rh);
            fileRepository.save(rh4);

            // === DOSSIER: Offres d'Emploi ===
            FolderEntity jobOffers = new FolderEntity();
            jobOffers.setName("Offres d'Emploi");
            jobOffers = folderRepository.save(jobOffers);

            FileEntity job1 = new FileEntity();
            job1.setName("Dev_Java_Senior.docx");
            job1.setFolder(jobOffers);
            fileRepository.save(job1);

            FileEntity job2 = new FileEntity();
            job2.setName("DevOps_Engineer.pdf");
            job2.setFolder(jobOffers);
            fileRepository.save(job2);

            FileEntity job3 = new FileEntity();
            job3.setName("Product_Manager.docx");
            job3.setFolder(jobOffers);
            fileRepository.save(job3);

            // === DOSSIER: Candidatures ===
            FolderEntity applications = new FolderEntity();
            applications.setName("Candidatures");
            applications = folderRepository.save(applications);

            FileEntity app1 = new FileEntity();
            app1.setName("CV_Marie_Dupont.pdf");
            app1.setFolder(applications);
            fileRepository.save(app1);

            FileEntity app2 = new FileEntity();
            app2.setName("CV_Jean_Martin.pdf");
            app2.setFolder(applications);
            fileRepository.save(app2);

            FileEntity app3 = new FileEntity();
            app3.setName("Lettre_Motivation_Claire.docx");
            app3.setFolder(applications);
            fileRepository.save(app3);

            // === DOSSIER: Données Financières ===
            FolderEntity finance = new FolderEntity();
            finance.setName("Données Financières");
            finance = folderRepository.save(finance);

            FileEntity fin1 = new FileEntity();
            fin1.setName("Budget_2026.xlsx");
            fin1.setFolder(finance);
            fileRepository.save(fin1);

            FileEntity fin2 = new FileEntity();
            fin2.setName("Bilan_2025.pdf");
            fin2.setFolder(finance);
            fileRepository.save(fin2);

            FileEntity fin3 = new FileEntity();
            fin3.setName("Rapport_Financier_Q1.docx");
            fin3.setFolder(finance);
            fileRepository.save(fin3);

            // === DOSSIER: Projets ===
            FolderEntity projects = new FolderEntity();
            projects.setName("Projets");
            projects = folderRepository.save(projects);

            FileEntity proj1 = new FileEntity();
            proj1.setName("Projet_Modernisation.pdf");
            proj1.setFolder(projects);
            fileRepository.save(proj1);

            FileEntity proj2 = new FileEntity();
            proj2.setName("Roadmap_Technique_2026.docx");
            proj2.setFolder(projects);
            fileRepository.save(proj2);

            FileEntity proj3 = new FileEntity();
            proj3.setName("Architecture_System.pptx");
            proj3.setFolder(projects);
            fileRepository.save(proj3);

            // === DOSSIER: Templates ===
            FolderEntity templates = new FolderEntity();
            templates.setName("Templates");
            templates = folderRepository.save(templates);

            FileEntity tmpl1 = new FileEntity();
            tmpl1.setName("Template_Email_Rejet.docx");
            tmpl1.setFolder(templates);
            fileRepository.save(tmpl1);

            FileEntity tmpl2 = new FileEntity();
            tmpl2.setName("Template_Contrat.pdf");
            tmpl2.setFolder(templates);
            fileRepository.save(tmpl2);

            FileEntity tmpl3 = new FileEntity();
            tmpl3.setName("Template_Offre_Emploi.docx");
            tmpl3.setFolder(templates);
            fileRepository.save(tmpl3);

            System.out.println("✓ FolderSeeder : " + folderRepository.count() + " dossiers et " +
                             fileRepository.count() + " fichiers créés");
        }
    }
}
