package org.crashoverride.battleship.algorithm;

import org.crashoverride.battleship.models.Point;

import java.util.ArrayList;
import java.util.List;

public class FrequencyGrid {
    private static final int GUESSED = -1;
    private static final int HIT = -4;
    int[][] grid;

    public FrequencyGrid(int size) {
        this.grid = new int[size][size];
    }

    public void setGuessedCells(List<Point> cells){
        for (Point cell: cells){
            grid[cell.x][cell.y] = GUESSED;
        }
    }

    public void setHitCells(List<Point> cells){
        for (Point cell: cells){
            grid[cell.x][cell.y] = HIT;
        }
    }

    public void increment(Point cell){
        grid[cell.x][cell.y] = grid[cell.x][cell.y] + 1;
    }

    public List<Point> getBestCells(){
        int max = Integer.MIN_VALUE;
        List<Point> bestCells = new ArrayList<>();
        for (int i = 0; i< grid.length;i++){
            for (int j = 0; j< grid[i].length; j++){
                int value = grid[i][j];
                if (value > max){
                    max = value;
                    bestCells = new ArrayList<>();
                    bestCells.add(new Point(i,j));
                } else if (value == max){
                    bestCells.add(new Point(i,j));
                }
            }
        }
        return bestCells;
    }

}
