package org.ups.citasaludservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CitasaludServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitasaludServiceApplication.class, args);
    }

}
