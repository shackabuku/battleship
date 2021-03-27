package org.crashoverride.battleship.algorithm;

import lombok.AllArgsConstructor;
import org.crashoverride.battleship.kafka.BattleshipTemplate;
import org.crashoverride.battleship.models.Point;

import java.util.List;
import java.util.Map;
import java.util.Random;

@AllArgsConstructor
public class MonteCarlo {
    private final int sampleSize;
    private final int gridSize;
    private final BattleshipTemplate battleshipTemplate;

    private Point getGuess(ShotGrid shots){
        List<Point> targetCells = shots.getTargetedCells();
        if (!targetCells.isEmpty()){
            return getRandomGuess(targetCells);
        } else {
            return getMonteCarloGuess(shots);
        }
    }

    public Point getRandomGuess( List<Point> targetPoints){
        Random random  = new Random();
        return targetPoints.get(random.nextInt(targetPoints.size()));
    }

    public Point getMonteCarloGuess(ShotGrid shots){
        FrequencyGrid frequencyGrid = new FrequencyGrid(gridSize);
        frequencyGrid.setGuessedCells(shots.getGuessed());
        for (int i = 0; i< sampleSize; i++){
            PlacementGrid placementGrid =  new PlacementGrid(gridSize, battleshipTemplate);
            for (Point point: shots.getGuessed()) {
                placementGrid.setCell(point, PlacementGrid.SKIP);
            }
            Map<Point,Integer> placementValues = placementGrid.getValues();
            for (Point point: placementValues.keySet()){
                if (placementValues.get(point) == PlacementGrid.PLACEMENT){
                    frequencyGrid.increment(point);
                }
            }
        }
        List<Point> bestCells = frequencyGrid.getBestCells();
        Random random = new Random();
        return bestCells.get(random.nextInt(bestCells.size()));

    }

}
