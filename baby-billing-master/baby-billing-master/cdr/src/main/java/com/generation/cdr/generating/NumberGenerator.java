package com.generation.cdr.generating;

import com.generation.cdr.enums.CallParticipantType;
import com.generation.cdr.store.entities.ExternalClientEntity;
import com.generation.cdr.store.entities.InternalClientEntity;
import com.generation.cdr.store.repositories.ExternalClientRepository;
import com.generation.cdr.store.repositories.InternalClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
class NumberGenerator {

    private final ExternalClientRepository externalClientRepository;
    private final InternalClientRepository internalClientRepository;

    @Autowired
    public NumberGenerator(ExternalClientRepository externalClientRepository, InternalClientRepository internalClientRepository) {
        this.externalClientRepository = externalClientRepository;
        this.internalClientRepository = internalClientRepository;
    }

    public CallParticipantType generateCallParticipantType(Random random) {
        int numberOfTypes = CallParticipantType.values().length;

        return CallParticipantType.values()[random.nextInt(numberOfTypes)];
    }

    public String[] generateNumbers(CallParticipantType callParticipantType, Random random) {
        String[] call = new String[2];

        List<ExternalClientEntity> externalClients = externalClientRepository.findAll();
        List<InternalClientEntity> internalClients = internalClientRepository.findAll();

        switch (callParticipantType) {
            case EXTERNAL_AND_EXTERNAL -> {
                ExternalClientEntity externalClient = getRandomElement(externalClients, random);
                call[0] = externalClient.getNumber();
                externalClients.remove(externalClient);
                call[1] = getRandomElement(externalClients, random).getNumber();
            }
            case EXTERNAL_AND_INTERNAL -> {
                call[0] = getRandomElement(externalClients, random).getNumber();
                call[1] = getRandomElement(internalClients, random).getNumber();
            }
            case INTERNAL_AND_EXTERNAL -> {
                call[0] = getRandomElement(internalClients, random).getNumber();
                call[1] = getRandomElement(externalClients, random).getNumber();
            }
            case INTERNAL_AND_INTERNAL -> {
                InternalClientEntity internalClient = getRandomElement(internalClients, random);
                call[0] = internalClient.getNumber();
                internalClients.remove(internalClient);
                call[1] = getRandomElement(internalClients, random).getNumber();
            }
        }
        return call;
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        if (list.isEmpty()) {
            System.err.println("List is empty");
        }
        return list.get(random.nextInt(list.size()));
    }
}
