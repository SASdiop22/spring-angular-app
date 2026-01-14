package edu.miage.springboot.seeders;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class EmployeSeeder implements CommandLineRunner {
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional // Important pour rattacher l'entité au contexte actuel
    public void run(String... args) throws Exception {
        if (employeRepository.count() > 0) return;

        UserEntity userRh = userRepository.findByUsername("alice.rh")
                .orElseThrow(() -> new RuntimeException("User alice.rh non trouvé"));
        UserEntity userAdmin = userRepository.findByUsername("bob.admin")
                .orElseThrow(() -> new RuntimeException("User bob.admin non trouvé"));

        // 2. Création de l'Employé RH
        EmployeEntity empRh = new EmployeEntity();
        empRh.setUser(userRh);
        empRh.setPoste("Responsable RH");
        empRh.setDepartement("RH");
        empRh.setRhPrivilege(true);
        // On sauvegarde d'abord l'employé RH pour qu'il ait une existence en base
        empRh = employeRepository.save(empRh);

        // 3. Création de l'Employé Admin (Le manager/référent)
        EmployeEntity empAdmin = new EmployeEntity();
        empAdmin.setUser(userAdmin); // Correction : on utilise userAdmin ici
        empAdmin.setPoste("Technicien Système");
        empAdmin.setDepartement("Informatique");
        empAdmin.setAdminPrivilege(true);

        UserEntity userCathy = userRepository.findByUsername("cathy.employe").orElseThrow();
        EmployeEntity empCathy = new EmployeEntity();
        empCathy.setUser(userCathy);
        empCathy.setPoste("Développeur");
        empCathy.setDepartement("IT");
        employeRepository.save(empCathy);

        // 4. Ajout de empRh dans les recrues liées de empAdmin (Spécification 5)
        // Cela établit le lien : empAdmin est le "référent" de empRh
        empAdmin.getRecruesLiees().add(userRh);

        // Si votre entité User possède aussi le champ referentEmploye (Onboarding) :
        userRh.setReferentEmploye(empAdmin);

        // 5. Sauvegarde finale
        employeRepository.save(empAdmin);
        userRepository.save(userRh);
    }
}