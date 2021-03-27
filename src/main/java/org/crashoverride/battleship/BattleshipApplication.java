package org.crashoverride.battleship;

import org.crashoverride.battleship.kafka.KafkaHandler;
import org.crashoverride.battleship.rest.RestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BattleshipApplication implements CommandLineRunner {

    private final KafkaHandler kafka;
    private final RestClient restClient;

    public BattleshipApplication(KafkaHandler kafka, RestClient restClient) {
        this.kafka = kafka;
        this.restClient = restClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(BattleshipApplication.class, args);
    }

    @Override
    public void run(String[] args) {
        restClient.registerTournament();
    }
}
