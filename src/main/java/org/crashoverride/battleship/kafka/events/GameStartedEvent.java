package org.crashoverride.battleship.kafka.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.crashoverride.battleship.kafka.BattleshipTemplate;
import org.crashoverride.battleship.kafka.Cell;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GameStartedEvent {
    @JsonProperty("gameId")
    private String gameId;
    @JsonProperty("tournamentId")
    private String tournamentId;
    @JsonProperty("battlegroundSize")
    private int battlegroundSize;
    private Cell core;
    @JsonProperty("battleshipTemplate")
    private BattleshipTemplate battleshipTemplate;
}
