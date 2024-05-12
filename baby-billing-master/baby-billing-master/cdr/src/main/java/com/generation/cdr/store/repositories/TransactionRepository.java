package com.generation.cdr.store.repositories;

import com.generation.cdr.store.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByOrderByStartTimeAsc();
}
