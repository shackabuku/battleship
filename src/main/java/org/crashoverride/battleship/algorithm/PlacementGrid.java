package org.crashoverride.battleship.algorithm;

import org.crashoverride.battleship.kafka.BattleshipTemplate;
import org.crashoverride.battleship.kafka.Cell;
import org.crashoverride.battleship.kafka.Direction;
import org.crashoverride.battleship.models.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class PlacementGrid {
    public static final int SKIP = -1;
    public static final int PLACEMENT = 1;
    private static final int MAX_PLACEMENT_ATTEMPS = 100;
    private final int[][] grid;
    private static final Random random = new Random();
    private final BattleshipTemplate battleshipTemplate;

    public PlacementGrid( int gridSize, BattleshipTemplate battleshipTemplate) {
        grid = new int[gridSize][gridSize];
        this.battleshipTemplate = battleshipTemplate;
        initialize();
    }

    public Map<Point, Integer> getValues(){
        Map<Point, Integer> values = new HashMap<>();
        for (int i = 0; i< grid.length; i++){
            for (int j = 0; j< grid[i].length;j++){
                values.put(new Point(i,j), grid[i][j]);
            }
        }
        return values;
    }

    public boolean initialize(){
        boolean found = false;
        int attempts = 0;
        while (attempts < MAX_PLACEMENT_ATTEMPS){
            attempts++;
            int row = random.nextInt(grid.length);
            int column = random.nextInt(grid.length);
            int directionId = random.nextInt(4);
            Direction direction = Direction.NORTH;
            switch (directionId){
                case 1:
                    direction = Direction.EAST;
                    break;
                case 2:
                    direction  = Direction.SOUTH;
                    break;
                case 3:
                    direction = Direction.WEST;
                    break;
            }

            if (this.place(row, column, direction)){
                found = true;
                break;
            }
        }
        return found;
    }
    private boolean place(int row, int column, Direction direction) {
        boolean free = true;
        //determine rotation
        Cell[][] canvas = rotateTemplate(direction);
        for (int i = 0; i < canvas.length; i++) {
            for (int j = 0; j < canvas[i].length; j++) {
                if (!canvas[i][j].isEmpty() && grid[row+i][column+j] != 0) {
                    free=  false;
                    break;
                }
            }
            if (!free){
                break;
            }
        }
        if (free){
            for (int i = 0; i < canvas.length; i++) {
                for (int j = 0; j < canvas[i].length; j++) {
                    grid[row+i][column+j] = PLACEMENT;
                }
            }
        }
        return free;
    }

    private Cell[][] rotateTemplate(Direction direction) {
        if (direction == Direction.SOUTH) {
            Cell[][] newCanvas = new Cell[battleshipTemplate.getHeight()][battleshipTemplate.getWidth()];
            for (int i = newCanvas.length; i > 0; i--) {
                newCanvas[newCanvas.length - i] = battleshipTemplate.getCanvas()[i - 1];
            }
            return newCanvas;
        }
        if (direction == Direction.WEST) {
            Cell[][] newCanvas = new Cell[battleshipTemplate.getWidth()][battleshipTemplate.getHeight()];
            for (int i = 0; i < newCanvas.length; i++) {
                for (int j = 0; j < newCanvas[i].length; j++) {
                    newCanvas[j][i] = battleshipTemplate.getCanvas()[i][j];
                }
            }
        }
        if (direction == Direction.EAST) {
            Cell[][] newCanvas = new Cell[battleshipTemplate.getWidth()][battleshipTemplate.getHeight()];
            for (int i = newCanvas.length - 1; i >= 0; i--) {
                for (int j = 0; j < newCanvas[i].length; j++) {
                    newCanvas[j][i] = battleshipTemplate.getCanvas()[i][j];
                }
            }
        }

        return battleshipTemplate.getCanvas();
    }

    public void setCell(Point point, int value){
        this.grid[point.x][point.y] = value;
    }
}
