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

import java.util.Arrays;

@Component
@Order(2)
public class EmployeSeeder implements CommandLineRunner {
    @Autowired private EmployeRepository employeRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (employeRepository.count() > 0) return;

        // === EMPLOYÉS EXISTANTS (Scénarios) ===
        createEmploye("alice.rh", "RH", "Manager RH", "0711223344", true, false,
            Arrays.asList("Recrutement", "SIRH", "Paie", "Droit social"));

        createEmploye("bob.admin", "IT", "Administrateur Système", "0722334455", false, true,
            Arrays.asList("Infrastructure", "Linux", "Windows", "Sécurité"));

        createEmploye("cathy.employe", "Technique", "Ingénieur Technique", "0733445566", false, false,
            Arrays.asList("Java", "Architecture", "Design patterns"));

        createEmploye("dylan.demandeur", "Technique", "Responsable Recrutement Technique", "0744556677", true, false,
            Arrays.asList("Recrutement IT", "Technical Screening"));

        // === EMPLOYÉS SUPPLÉMENTAIRES (pour enrichir la base) ===
        createEmploye("emma.marketing", "Marketing", "Directrice Marketing", "0755667788", false, false,
            Arrays.asList("Marketing", "SEO", "Analytics", "Brand"));

        createEmploye("francois.finance", "Finance", "Responsable Finance", "0766778899", false, false,
            Arrays.asList("Gestion financière", "Budgeting", "Reporting", "Audit"));

        createEmploye("genevieve.ventes", "Sales", "Manager Ventes", "0777889900", false, false,
            Arrays.asList("Ventes B2B", "CRM", "Négociation", "Prospection"));

        createEmploye("henry.it", "IT", "Lead Developer", "0788990011", false, false,
            Arrays.asList("Java", "Spring Boot", "Kubernetes", "Clean Code"));

        createEmploye("isabelle.rh", "RH", "Chargée de Recrutement", "0799001122", false, false,
            Arrays.asList("Recrutement", "Sourcing", "LinkedIn", "Entretiens"));

        createEmploye("jerome.tech", "Technique", "Architect", "0700112233", false, false,
            Arrays.asList("Architecture", "Microservices", "Cloud", "DevOps"));

        createEmploye("kathleen.ops", "Operations", "Manager Operations", "0711223355", false, false,
            Arrays.asList("Operations", "Processus", "Amélioration continue", "Six Sigma"));

        System.out.println("✓ EmployeSeeder : " + employeRepository.count() + " employés créés");
    }

    private void createEmploye(
        String username,
        String department,
        String position,
        String telephone,
        boolean isRhPrivilege,
        boolean isAdminPrivilege,
        java.util.List<String> skills
    ) {
        var userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            System.out.println("⚠️  Utilisateur non trouvé pour employé : " + username + " (ignoré)");
            return;
        }

        UserEntity user = userOptional.orElseThrow();
        EmployeEntity employe = new EmployeEntity();
        employe.setUser(user);
        employe.setDepartement(department);
        employe.setPoste(position);
        employe.setTelephoneInterne(telephone);
        employe.setRhPrivilege(isRhPrivilege);
        employe.setAdminPrivilege(isAdminPrivilege);
        employe.setDemandeurDePoste(true);

        // Ajouter les compétences
        if (skills != null) {
            skills.forEach(skill -> employe.getSkills().add(skill));
        }

        employeRepository.save(employe);
    }
}