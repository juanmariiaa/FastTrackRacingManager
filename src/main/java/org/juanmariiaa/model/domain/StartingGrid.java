package org.juanmariiaa.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartingGrid {
    private int raceId;
    private List<GridPosition> gridPositions;

    public StartingGrid() {
        this.gridPositions = new ArrayList<>();  // Inicializa la lista vacía
    }

    public StartingGrid(int raceId, List<GridPosition> gridPositions) {
        this.raceId = raceId;
        this.gridPositions = gridPositions != null ? gridPositions : new ArrayList<>();
    }

    public void addGridPosition(GridPosition gridPosition) {
        if (gridPositions != null) {
            gridPositions.add(gridPosition);
        }
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public List<GridPosition> getGridPositions() {
        return gridPositions;
    }

    public void setGridPositions(List<GridPosition> gridPositions) {
        this.gridPositions = gridPositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartingGrid that = (StartingGrid) o;
        return raceId == that.raceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceId);
    }

    @Override
    public String toString() {
        return "StartingGrid{" +
                "raceId=" + raceId +
                ", gridPositions=" + gridPositions +
                '}';
    }
}
