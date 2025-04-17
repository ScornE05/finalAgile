//package com.example.petshop.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Configuration
//public class FileUploadConfig implements WebMvcConfigurer {
//
//    @Value("${app.upload.dir:src/main/resources/static/uploads/}")
//    private String uploadDir;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // This will make static resources accessible via the /uploads/** URL pattern
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" + System.getProperty("user.dir") + "/" + uploadDir);
//    }
//
//    @Bean
//    public void createUploadDirectories() {
//        // Create the main upload directory if it doesn't exist
//        File uploadDirectory = new File(uploadDir);
//        if (!uploadDirectory.exists()) {
//            uploadDirectory.mkdirs();
//        }
//
//        // Create subdirectories for products and pets
//        Path productsPath = Paths.get(uploadDir, "products");
//        File productsDir = productsPath.toFile();
//        if (!productsDir.exists()) {
//            productsDir.mkdirs();
//        }
//
//        Path petsPath = Paths.get(uploadDir, "pets");
//        File petsDir = petsPath.toFile();
//        if (!petsDir.exists()) {
//            petsDir.mkdirs();
//        }
//    }
//}
