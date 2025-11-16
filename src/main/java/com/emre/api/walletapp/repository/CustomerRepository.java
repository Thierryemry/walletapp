package com.emre.api.walletapp.repository;

import com.emre.api.walletapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
