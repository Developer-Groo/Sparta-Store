package com.example.Sparta_Store.payment.repository;

import com.example.Sparta_Store.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
