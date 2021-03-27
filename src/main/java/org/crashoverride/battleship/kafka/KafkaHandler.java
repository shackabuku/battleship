package org.crashoverride.battleship.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.crashoverride.battleship.kafka.events.*;
import org.crashoverride.battleship.models.Board;
import org.crashoverride.battleship.models.Point;
import org.crashoverride.battleship.rest.RestClient;
import org.crashoverride.battleship.rest.RestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.IntStream;

@Service
public class KafkaHandler {
    private static final String topicGameStarted = "cc.battleships.game.started";
    private static final String topicRoundStarted = "cc.battleships.round.started";
    private static final String topicShot = "cc.battleships.shot";
    private static final String topicRoundEnded = "cc.battleships.round.ended";
    private static final String topicGameEnded = "cc.battleships.game.ended";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String tournametId;
    private final Board gameBoard;
    private final RestClient restClient;

    public KafkaHandler(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, @Value("${tournament.id}") String tournametId, RestClient restClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.tournametId = tournametId;
        this.restClient = restClient;
        this.gameBoard = new Board();
    }

    public void sendMessage(String message, String accessToken) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topicShot, message);
        record.headers().add("Authorization", ("Bearer " + accessToken).getBytes());
        this.kafkaTemplate.send(record);
    }

    @SneakyThrows
    @KafkaListener(topics = topicGameStarted)
    public void consumeGameStarted(String message) {
        GameStartedEvent gameStartedEvent = objectMapper.reader().forType(GameStartedEvent.class).readValue(message);
        if (!gameStartedEvent.getTournamentId().equals(tournametId)) {
            return;
        }


        //board size - init
        gameBoard.init(gameStartedEvent.getBattlegroundSize());

        //battleship design - init
        BattleshipTemplate template = gameStartedEvent.getBattleshipTemplate();

        Random r = new Random();

        int retries = 10;
        while(retries > 0) {
            retries--;
            String gameId = gameStartedEvent.getGameId();
            int xPos = r.nextInt(gameStartedEvent.getBattlegroundSize() - template.getWidth());
            int yPos = r.nextInt(gameStartedEvent.getBattlegroundSize() - template.getHeight());
            String direction = "SOUTH";

            RestResponse rsvp = restClient.placeBattleship(gameId, xPos, gameBoard.getSize() - 1 - yPos, direction);

            if (rsvp.getStatus() == 200) {
                IntStream.range(xPos, xPos + template.getWidth()).forEach(x ->
                        IntStream.range(yPos, yPos + template.getHeight()).forEach(y ->
                                gameBoard.excludeAvailableHits(new Point(x, y))));
                break;
            }
        }
    }

    @SneakyThrows
    @KafkaListener(topics = topicRoundStarted)
    public void consumeRoundStarted(String message) {
        RoundStartEvent event = objectMapper.reader().forType(RoundStartEvent.class).readValue(message);
        if (!event.getTournamentId().equals(tournametId)) {
            return;
        }

        Random r = new Random();

        // calculate shoot position
        int available = gameBoard.getAvailableHits().size();
        Point p = gameBoard.getAvailableHits().get(r.nextInt(available));
        gameBoard.excludeAvailableHits(p);

        //send shoot event
        ShootEvent shootEvent = new ShootEvent(event.getGameId(), event.getTournamentId(), event.getRoundNo(), p.x, p.y);
        String serializedShootEvent = objectMapper.writeValueAsString(shootEvent);
        this.sendMessage(serializedShootEvent, restClient.getAccessToken());
    }

    @SneakyThrows
    @KafkaListener(topics = topicRoundEnded)
    public void consumeRoundEnded(String message) {
        RoundEndedEvent roundEndedEvent = objectMapper.reader().forType(RoundEndedEvent.class).readValue(message);
        if (!roundEndedEvent.getTournamentId().equals(tournametId)) {
            return;
        }


        // update shoit
    }

    @SneakyThrows
    @KafkaListener(topics = topicGameEnded)
    public void consumeGameEnded(String message) {
        GameEndedEvent gameEndedEvent = objectMapper.reader().forType(GameEndedEvent.class).readValue(message);
        if (!gameEndedEvent.getTournamentId().equals(tournametId)) {
            return;
        }

        // cleanup
    }

}
