
package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.EcommerceAdminVo;
import com.example.demo.repository.EcommerceAdminRepository;

@Configuration
public class EcommerceAdminSeeder {

    @Bean
    public CommandLineRunner createEcommerceAdmin(
            EcommerceAdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "admin@example.com";

            if (adminRepository.existsByEmailIgnoreCase(email)) {
                System.out.println("E-Commerce Admin already exists.");
                return;
            }

            EcommerceAdminVo admin = new EcommerceAdminVo();

            admin.setName("E-Commerce Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setActive(true);

            adminRepository.save(admin);

            System.out.println("E-Commerce Admin created successfully.");
        };
    }
}
