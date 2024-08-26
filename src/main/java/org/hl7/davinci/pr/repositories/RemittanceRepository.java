package org.hl7.davinci.pr.repositories;

import org.hl7.davinci.pr.domain.Remittance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemittanceRepository extends JpaRepository<Remittance, Integer> {
}
