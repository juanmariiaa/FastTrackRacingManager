package org.juanmariiaa.model.domain;

public class GridPosition {
    private int position;
    private String driverDni;
    private int racingTeamId;

    public GridPosition(int position, String driverDni, int racingTeamId) {
        this.position = position;
        this.driverDni = driverDni;
        this.racingTeamId = racingTeamId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDriverDni() {
        return driverDni;
    }

    public void setDriverDni(String driverDni) {
        this.driverDni = driverDni;
    }

    public int getRacingTeamId() {
        return racingTeamId;
    }

    public void setRacingTeamId(int racingTeamId) {
        this.racingTeamId = racingTeamId;
    }

    @Override
    public String toString() {
        return "GridPosition{" +
                "position=" + position +
                ", driverDni='" + driverDni + '\'' +
                ", racingTeamId=" + racingTeamId +
                '}';
    }
}
