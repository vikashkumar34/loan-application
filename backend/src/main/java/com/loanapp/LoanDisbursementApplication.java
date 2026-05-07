package com.loanapp;

import com.loanapp.entity.Role;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LoanDisbursementApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanDisbursementApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@example.com");
                admin.setFullName("Admin User");
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin user created successfully.");
            }
        };
    }
}
