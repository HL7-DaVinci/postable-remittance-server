package org.hl7.davinci.pr.repositories;

import org.hl7.davinci.pr.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
