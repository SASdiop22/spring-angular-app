package edu.miage.springboot.seeders.users;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        UserEntity userDylan = userRepository.findByUsername("dylan.demandeur")
                .orElseThrow(() -> new RuntimeException("User dylan.demandeur non trouvé"));
        UserEntity userCathy = userRepository.findByUsername("cathy.employe").orElseThrow();



        // 2. Création de l'Employé RH
        EmployeEntity empRh = new EmployeEntity();
        empRh.setUser(userRh);
        empRh.setPoste("Responsable RH");
        empRh.setDepartement("RH");
        empRh.setRhPrivilege(true);
        // On sauvegarde d'abord l'employé RH pour qu'il ait une existence en base
        empRh = employeRepository.save(empRh);

        EmployeEntity empCathy = new EmployeEntity();
        empCathy.setUser(userCathy);
        empCathy.setPoste("Développeur");
        empCathy.setDepartement("IT");
        employeRepository.save(empCathy);

        EmployeEntity empDylan = new EmployeEntity();
        empDylan.setUser(userDylan);
        empDylan.setPoste("Manager IT / Demandeur");
        empDylan.setDepartement("Informatique");
        // Si votre entité possède ce champ (vu dans vos logs Hibernate : demandeur_de_poste)
        empDylan.setDemandeurDePoste(true);
        employeRepository.save(empDylan);

        // 3. Création de l'Employé Admin (Le manager/référent)
        EmployeEntity empAdmin = new EmployeEntity();
        empAdmin.setUser(userAdmin); // Correction : on utilise userAdmin ici
        empAdmin.setPoste("Technicien Système");
        empAdmin.setDepartement("Informatique");
        empAdmin.setAdminPrivilege(true);
        empAdmin = employeRepository.save(empAdmin);


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