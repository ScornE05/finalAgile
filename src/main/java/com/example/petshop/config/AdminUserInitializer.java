package com.example.petshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.petshop.entity.User;
import com.example.petshop.repository.UserRepository;

/**
 * This component initializes an admin user when the application starts
 * if no admin user exists in the database.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Checking for admin user existence...");
        try {
            boolean adminExists = userRepository.existsByUsername(adminUsername);
            System.out.println("Admin exists check result: " + adminExists);

            if (!adminExists) {
                System.out.println("Admin user does not exist, creating new admin user...");
                // Create admin user
                User adminUser = new User();
                adminUser.setUsername(adminUsername);
                adminUser.setEmail(adminEmail);
                adminUser.setPassword(adminPassword); // Không mã hóa nữa
                adminUser.setFullName("Admin User");
                adminUser.setRole("ADMIN");

                User savedUser = userRepository.save(adminUser);
                System.out.println("Admin user created successfully with ID: " + savedUser.getId() + " and username: " + savedUser.getUsername());
            } else {
                // Cập nhật mật khẩu admin hiện có
                userRepository.findByUsername(adminUsername).ifPresent(admin -> {
                    admin.setPassword(adminPassword); // Không mã hóa
                    userRepository.save(admin);
                    System.out.println("Updated admin password to plain text for learning purposes.");
                });
            }
        } catch (Exception e) {
            System.err.println("Error during admin user initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
