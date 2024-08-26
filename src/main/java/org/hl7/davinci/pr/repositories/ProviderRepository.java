package org.hl7.davinci.pr.repositories;

import org.hl7.davinci.pr.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {
}
