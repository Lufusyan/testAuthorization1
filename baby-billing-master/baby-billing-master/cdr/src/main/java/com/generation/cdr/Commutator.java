package com.generation.cdr;

import com.generation.cdr.kafka.KafkaProducer;
import com.generation.cdr.minio.Minio;
import com.generation.cdr.utils.LastModifiedComparator;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class Commutator {
    private final KafkaProducer kafkaProducer;
    private final LastModifiedComparator lastModifiedComparator;
    private final Minio minio;

    @Autowired
    public Commutator(KafkaProducer kafkaProducer, Minio minio, LastModifiedComparator lastModifiedComparator) {
        this.kafkaProducer = kafkaProducer;
        this.minio = minio;
        this.lastModifiedComparator = lastModifiedComparator;
    }

    private Iterable<Result<Item>> listFiles(String bucket) {
        return minio.listObjects(bucket);
    }

    private List<Item> sortByLastModified(List<Item> items) {
        items.sort(lastModifiedComparator);
        return items;
    }

    public void sendToTariffication(String bucket) {
        List<Item> items = getFilesFromMinio(bucket);
        for (Item item : items) {
            String fileName = item.objectName();
            sendToBroker(bucket, fileName);
        }
    }

    private List<Item> getFilesFromMinio(String bucket) {
        Iterable<Result<Item>> list = listFiles(bucket);
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> itemResult : list) {
                items.add(itemResult.get());
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return sortByLastModified(items);
    }


    private void sendToBroker(String bucket, String fileName) {
        Scanner scanner = new Scanner(minio.getObject(bucket, fileName));
        while (scanner.hasNext()) {
            kafkaProducer.sendMessage(scanner.next());
        }
    }
}
