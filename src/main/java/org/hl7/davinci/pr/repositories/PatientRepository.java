package org.hl7.davinci.pr.repositories;

import org.hl7.davinci.pr.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
}
