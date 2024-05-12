package com.authorization.brt.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String phoneNumber;

    @Column
    private String name;

    @Column
    private long balance;

    @Column
    private int tariffId; //возможно ли сделать связь с таблицей tariff?

    public ClientEntity(int id, String phoneNumber, String name, long balance,int tariffId) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.tariffId = tariffId;
    }
}
