package com.generation.cdr.store.repositories;

import com.generation.cdr.store.entities.ExternalClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalClientRepository extends JpaRepository<ExternalClientEntity, Long> {
}
