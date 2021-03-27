package org.crashoverride.battleship.kafka.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RoundStartEvent {

    @JsonProperty("gameId")
    private String gameId;
    @JsonProperty("tournamentId")
    private String tournamentId;
    int roundNo;
}
