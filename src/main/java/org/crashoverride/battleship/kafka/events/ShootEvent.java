package org.crashoverride.battleship.kafka.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShootEvent {
    @JsonProperty("gameId")
    private String gameId;
    @JsonProperty("tournamentId")
    private String tournamentId;
    @JsonProperty("roundNo")
    int roundNo;
    private int x;
    private int y;

}
