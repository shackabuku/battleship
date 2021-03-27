package org.crashoverride.battleship.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Shot {
    private int x;
    private int y;
    @JsonProperty("status")
    private ShotStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Shot shot = (Shot) o;
        return x == shot.x && y == shot.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

