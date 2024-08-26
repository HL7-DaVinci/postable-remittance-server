package org.hl7.davinci.pr.repositories;

import org.hl7.davinci.pr.domain.ClaimQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimQueryRepository extends JpaRepository<ClaimQuery, Integer> {
}
