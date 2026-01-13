package edu.miage.springboot.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.repositories.UserRepository;

public class UserServiceImpl {


    @Autowired
    private UserRepository userRepository;
    
    public void checkUserExists(String username) {
    Optional<UserEntity> userOpt = userRepository.findByUsername(username);
    
    if (userOpt.isEmpty()) { 
        // Ici, .isEmpty() est une méthode native de Optional de Java
        System.out.println("Aucun utilisateur trouvé avec ce pseudo.");
    }
}
    
}
