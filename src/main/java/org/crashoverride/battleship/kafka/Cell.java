package org.crashoverride.battleship.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Cell {
    private String symbol;
    private int hp;

    public boolean isEmpty(){
        return symbol.equals(" ");
    }
}
