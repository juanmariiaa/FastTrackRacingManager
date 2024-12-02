package org.juanmariiaa.model.domain;

import java.util.List;
import java.util.Objects;

public class CarRace {
    private int id;
    private String name;
    private String location;
    private String city;
    private String date; // Cambiado de Date a String
    private List<RacingTeam> racingTeams;

    public CarRace() {
        this.id = 0;
        this.name = "";
        this.location = "";
        this.city = "";
        this.date = null;  // Ahora es String, inicializado en null
        this.racingTeams = null;
    }

    public CarRace(String name, String location, String city, String date, List<RacingTeam> racingTeams) {
        this.id = 0;
        this.name = name;
        this.location = location;
        this.city = city;
        this.date = date;  // Ahora se recibe un String para la fecha
        this.racingTeams = racingTeams;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {  // Modificado para devolver un String
        return date;
    }

    public void setDate(String date) {  // Modificado para aceptar un String
        this.date = date;
    }

    public List<RacingTeam> getTeams() {
        return racingTeams;
    }

    public void setTeams(List<RacingTeam> racingTeams) {
        this.racingTeams = racingTeams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarRace that = (CarRace) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CarRace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +  // Cambiado para mostrar String
                ", teams=" + racingTeams +
                '}';
    }
}
