package com.authorization.brt.kafka;

import com.authorization.brt.service.ClientService;
import com.authorization.brt.store.entities.ClientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class KafkaConsumerTest {

    @Mock
    private ClientService clientService;
    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        kafkaConsumer = new KafkaConsumer(clientService);
    }

    @Test
    public void testListen_ClientFound() {
        // Arrange
        String msg = "01, 79999999999, 79999999998, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "01, 79999999999, 79999999998, 1709798657, 1709799601, 11";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }

    @Test
    public void testListen_ClientNotFound() {
        String msg = "01, 79999999999, 79999999998, 1709798657, 1709799601";
        ClientEntity clientEntity = new ClientEntity(1,"79999999998", "Ivan", 100, 11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(null, kafkaConsumer.getMsgWithTariffId());
    }

    @Test
    public void testListen_Number1Less() {
        // Arrange
        String msg = "01, 69999999999, 79999999998, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"69999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }

    @Test
    public void testListen_Number1More() {
        // Arrange
        String msg = "01, 80000000000, 79999999998, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"80000000000", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }

    @Test
    public void testListen_Number2Less() {
        // Arrange
        String msg = "01, 79999999999, 69999999999, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_Number2More() {
        // Arrange
        String msg = "01, 79999999999, 80000000000, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"80000000000", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_StartTimeMore() {
        // Arrange
        String msg = "01, 79999999999, 79999999998, 1735678799, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_StartTimeLess() {
        // Arrange
        String msg = "01, 79999999999, 79999999998, 1704056399, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_EndTimeMore() {
        // Arrange
        String msg = "01, 79999999999, 79999999998, 1709798657, 1735678800";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_EndTimeLess() {
        // Arrange
        String msg = "01, 79999999999, 79999999998, 1709798657, 1704056400";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_TypeCallNotCorrect() {
        // Arrange
        String msg = "03, 79999999999, 79999999998, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_Number1String() {
        // Arrange
        String msg = "01, hellooooooo, 79999999998, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999999", "Ivan", 100, 11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_Number2String() {
        // Arrange
        String msg = "01, 79999999998, hellooooooo, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999998", "Ivan", 100, 11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_TypeCallString() {
        // Arrange
        String msg = "hо, 79999999998, 79999999999, 1709798657, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999998", "Ivan", 100, 11);
        clientEntity.setTariffId(11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_StartTimeString() {
        // Arrange
        String msg = "01, 79999999998, 79999999999, hellooooooo, 1709799601";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999998", "Ivan", 100, 11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
    @Test
    public void testListen_EndTimeString() {
        // Arrange
        String msg = "01, 79999999998, 79999999999, 1709799601, hellooooooo";
        String expectedMsgWithTariffId = "Error";
        ClientEntity clientEntity = new ClientEntity(1,"79999999998", "Ivan", 100, 11);
        when(clientService.findClientByPhoneNumber(anyString())).thenReturn(Optional.of(clientEntity));
        kafkaConsumer.listen(msg);
        assertEquals(expectedMsgWithTariffId, kafkaConsumer.getMsgWithTariffId());
    }
}