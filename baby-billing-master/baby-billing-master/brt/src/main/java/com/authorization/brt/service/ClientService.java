package com.authorization.brt.service;

import com.authorization.brt.store.entities.ClientEntity;
import com.authorization.brt.store.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<ClientEntity> findClientByPhoneNumber(String number) {
        return clientRepository.findByPhoneNumber(number);
    }
}
