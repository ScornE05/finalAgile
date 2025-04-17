//package com.example.petshop.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.example.petshop.entity.User;
//import com.example.petshop.repository.UserRepository;
//
///**
// * This component initializes an admin user when the application starts
// * if no admin user exists in the database.
// */
//@Component
//public class AdminUserInitializer implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Value("${app.admin.username:admin}")
//    private String adminUsername;
//
//    @Value("${app.admin.email:admin@example.com}")
//    private String adminEmail;
//
//    @Value("${app.admin.password:admin123}")
//    private String adminPassword;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Check if admin user exists
//        if (!userRepository.existsByUsername(adminUsername)) {
//            // Create admin user
//            User adminUser = new User();
//            adminUser.setUsername(adminUsername);
//            adminUser.setEmail(adminEmail);
//            adminUser.setPassword(passwordEncoder.encode(adminPassword));
//            adminUser.setFullName("Admin User");
//            adminUser.setRole("ADMIN");
//
//            userRepository.save(adminUser);
//
//            System.out.println("Admin user created with username: " + adminUsername);
//        }
//    }
//}
