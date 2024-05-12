package com.authorization.brt.kafka;

import com.authorization.brt.service.ClientService;
import com.authorization.brt.store.entities.ClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumer {
    private String msgWithTariffId;
    private final ClientService clientService;

    @Autowired
    public KafkaConsumer(ClientService clientService) {
        this.clientService = clientService;
    }

    @KafkaListener(topics = "cdr-records", groupId = "group-1")
    public void listen(String msg) {
        String number = msg.substring(3, 14);
        Optional<ClientEntity> client = clientService.findClientByPhoneNumber(number);
        if (client.isPresent()) {
            msgWithTariffId = msg + ", " + client.get().getTariffId();
            // авторизованный клиент
            // отправить в hrs
        }

    }

    public String getMsgWithTariffId() {
        return msgWithTariffId;
    }
}