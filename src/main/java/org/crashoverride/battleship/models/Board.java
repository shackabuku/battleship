package org.crashoverride.battleship.models;

import lombok.extern.slf4j.Slf4j;
import org.crashoverride.battleship.kafka.Shot;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Board {
    private String[][] board;
    private Set<Shot> hitList;
    private List<Point> availableHits;
    private int size = 0;

    public void init(int n) {
        this.size = n;
        board = new String[n][n];
        hitList = new HashSet<>();
        availableHits = new ArrayList<>();
        IntStream.range(0, size).forEach(x -> IntStream.range(0, size).forEach(y -> availableHits.add(new Point(x, y))));
    }

    public int getSize() {
        return size;
    }

    public List<Point> getAvailableHits() {
        return availableHits;
    }

    public void update(List<Shot> shotList) {
        shotList.forEach(s -> board[s.getX()][s.getY()] = s.getStatus().name());
        hitList.addAll(shotList);
    }

    public void prettyPrint() {
        log.info(Arrays.stream(board).map(l -> Arrays.stream(l).map(p -> p.substring(0, 1)).collect(Collectors.joining(" "))).collect(Collectors.joining("\n")));
    }

    public void excludeAvailableHits(Point point) {
        availableHits = availableHits.stream().filter(p -> !p.equals(point)).collect(Collectors.toList());
    }

}
