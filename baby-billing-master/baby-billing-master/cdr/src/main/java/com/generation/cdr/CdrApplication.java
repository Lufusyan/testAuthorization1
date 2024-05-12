package com.generation.cdr;

import com.generation.cdr.generating.CDRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CdrApplication implements CommandLineRunner {

    private final Commutator commutator;
    private final ApplicationContext applicationContext;

    @Value("${bucket}")
    private String bucket;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    public CdrApplication(Commutator commutator, ApplicationContext applicationContext) {
        this.commutator = commutator;
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(CdrApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (activeProfile.equals("dev")) {
            applicationContext.getBean(CDRGenerator.class).generateCDRFiles();
        }
        commutator.sendToTariffication(bucket);
    }
}
