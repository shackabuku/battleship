package org.crashoverride.battleship.algorithm;

import org.crashoverride.battleship.kafka.Cell;
import org.crashoverride.battleship.kafka.Shot;
import org.crashoverride.battleship.kafka.ShotStatus;
import org.crashoverride.battleship.models.Point;

import java.util.ArrayList;
import java.util.List;

public class ShotGrid {
    private static final String HIT = "X";
    private static final String MISS = "*";
    private static final String UNGUESSED = " ";
    private String[][] grid;

    public ShotGrid(int size, List<Shot> shots) {
        grid = new String[size][size];
        for (Shot shot: shots){
            String value = UNGUESSED;
            if (shot.getStatus() == ShotStatus.HIT || shot.getStatus() == ShotStatus.KILL){
                value = HIT;
            }
            if (shot.getStatus() == ShotStatus.MISS){
                value = MISS;
            }
            grid[shot.getX()][shot.getY()] = value;
        }
        for (int i=0;i< grid.length;i++){
            for (int j=0;j<grid[i].length;j++){
                if (grid[i][j] == null){
                    grid[i][j] = UNGUESSED;
                }
            }
        }
    }

    public List<Point> getHits(){
        return getAllPointsMatching(HIT);
    }

    public List<Point> getMissed(){
        return getAllPointsMatching(MISS);
    }

    public List<Point> getGuessed(){
        List<Point> guessedPoints = new ArrayList<>();
        guessedPoints.addAll(getHits());
        guessedPoints.addAll(getMissed());
        return guessedPoints;
    }

    private List<Point> getAllPointsMatching(String value){
        List<Point> points = new ArrayList<>();
        for (int i = 0; i<grid.length;i++){
            for (int j= 0; i<grid[i].length; j++){
                if (grid[i][j].equals(HIT)){
                    points.add(new Point(i,j));
                }
            }
        }
        return points;
    }



    public List<Point> getUnguessedCells(){
        return getAllPointsMatching(UNGUESSED);
    }

    public List<Point> getTargetedCells(){
        List<Point> targetCells = new ArrayList<>();
        List<Point> unguessedCells = getUnguessedCells();
        for (Point point: getHits()){
            Point adjacent = new Point(point.x-1, point.y);
            if (unguessedCells.contains(adjacent)){
                targetCells.add(adjacent);
            }
            adjacent = new Point(point.x+1, point.y);
            if (unguessedCells.contains(adjacent)){
                targetCells.add(adjacent);
            }
            adjacent = new Point(point.x, point.y -1);
            if (unguessedCells.contains(adjacent)){
                targetCells.add(adjacent);
            }
            adjacent = new Point(point.x, point.y+1);
            if (unguessedCells.contains(adjacent)){
                targetCells.add(adjacent);
            }

        }
        return targetCells;
    }

    public boolean guess(int row, int column, Cell[][] opponntsGrid) {
        boolean hit = true;
        if (opponntsGrid[row][column].isEmpty()) {
            this.grid[row][column] = MISS;
            hit = false;
        } else {
            this.grid[row][column] = HIT;
        }
        return hit;
    }

}
