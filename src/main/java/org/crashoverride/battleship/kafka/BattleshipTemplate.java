package org.crashoverride.battleship.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class BattleshipTemplate {
    Cell[][] canvas;
    int width;
    int height;
}
