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
//import java.util.Optional;
//
//@Component
//public class AdminUserUpdater implements CommandLineRunner {
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
//    @Value("${app.admin.password:admin123}")
//    private String adminPassword;
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("Updating admin user password...");
//        try {
//            Optional<User> adminUserOpt = userRepository.findByUsername(adminUsername);
//            if (adminUserOpt.isPresent()) {
//                User adminUser = adminUserOpt.get();
//                System.out.println("Found admin user with ID: " + adminUser.getId());
//
//                // Update password
//                String encodedPassword = passwordEncoder.encode(adminPassword);
//                adminUser.setPassword(encodedPassword);
//                userRepository.save(adminUser);
//
//                System.out.println("Admin password updated successfully. New encoded password: " + encodedPassword);
//            } else {
//                System.out.println("Admin user not found, something is wrong!");
//            }
//        } catch (Exception e) {
//            System.err.println("Error updating admin password: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}