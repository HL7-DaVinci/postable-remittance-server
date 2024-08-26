package org.hl7.davinci.pr.repositories;

import org.hl7.davinci.pr.domain.Payer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayerRepository extends JpaRepository<Payer, Integer> {
}
