package com.emre.api.walletapp.seeder;

import com.emre.api.walletapp.model.AppUser;
import com.emre.api.walletapp.repository.AppUserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public ApplicationRunner seedDatabase(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.count() == 0) {

                // Employee kullanıcı
                AppUser employee = new AppUser();
                employee.setUsername("employee1");
                employee.setPassword(passwordEncoder.encode("employee123"));
                employee.setRole("EMPLOYEE");
                userRepository.save(employee);

                // Customer kullanıcı
                AppUser customer = new AppUser();
                customer.setUsername("customer1");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setRole("CUSTOMER");
                userRepository.save(customer);

                // Customer kullanıcı
                AppUser customer2 = new AppUser();
                customer2.setUsername("customer2");
                customer2.setPassword(passwordEncoder.encode("customer123"));
                customer2.setRole("CUSTOMER");
                userRepository.save(customer2);
            }
        };
    }
}