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
    @Autowired private EmployeRepository employeRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (employeRepository.count() > 0) return;

        createEmp("alice.rh", "RH", true, false);
        createEmp("bob.admin", "IT", false, true);
        createEmp("cathy.employe", "Ventes", false, false);
        createEmp("dylan.demandeur", "Technique", false, false);
    }

    private void createEmp(String username, String dept, boolean isRh, boolean isAdmin) {
        UserEntity u = userRepository.findByUsername(username).orElseThrow();
        EmployeEntity e = new EmployeEntity();
        e.setUser(u);
        e.setDepartement(dept);
        e.setPoste("Staff " + dept);
        e.setRhPrivilege(isRh);
        e.setAdminPrivilege(isAdmin);
        employeRepository.save(e);
    }

}