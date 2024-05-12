package com.generation.cdr.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String callType;

    @Column
    private String firstNumber;

    @Column
    private String secondNumber;

    @Column
    private long startTime;

    @Column
    private long endTime;
}
