package com.generation.cdr.services;

import com.generation.cdr.dto.TransactionDTO;
import com.generation.cdr.store.entities.TransactionEntity;
import com.generation.cdr.store.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void sortByStartDate() {
        List<TransactionEntity> transactionEntities = transactionRepository.findAllByOrderByStartTimeAsc();
        transactionRepository.deleteAll();
        transactionRepository.saveAll(transactionEntities);
    }

    public TransactionEntity toTransactionEntity(TransactionDTO transactionDTO) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setCallType(transactionDTO.getCallType());
        transactionEntity.setFirstNumber(transactionDTO.getFirstNumber());
        transactionEntity.setSecondNumber(transactionDTO.getSecondNumber());
        transactionEntity.setStartTime(transactionDTO.getStartTime());
        transactionEntity.setEndTime(transactionDTO.getEndTime());

        return transactionEntity;
    }
}
