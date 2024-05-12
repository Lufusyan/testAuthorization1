package com.generation.cdr.store.repositories;

import com.generation.cdr.store.entities.InternalClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InternalClientRepository extends JpaRepository<InternalClientEntity, Long> {
}
