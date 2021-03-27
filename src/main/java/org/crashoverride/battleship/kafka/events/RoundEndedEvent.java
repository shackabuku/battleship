package org.crashoverride.battleship.kafka.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.crashoverride.battleship.kafka.Shot;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RoundEndedEvent {

    @JsonProperty("gameId")
    private String gameId;
    @JsonProperty("tournamentId")
    private String tournamentId;
    int roundNo;
    List<Shot> shots;
}
