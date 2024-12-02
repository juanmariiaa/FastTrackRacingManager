package org.juanmariiaa.model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RacingTeam {
    private int id;
    private String name;
    private String city;
    private String institution;
    private List<Driver> drivers;
    private byte[] imageData;

    public RacingTeam() {
        this.id = 0;
        this.name = "";
        this.city = "";
        this.institution = "";
        this.drivers = new ArrayList<>();
        this.imageData = new byte[0];
    }

    public RacingTeam(int id, String name, String city, String institution, List<Driver> drivers, byte[] imageData) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.institution = institution;
        this.drivers = drivers;
        this.imageData = imageData;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public List<Driver> getParticipants() {
        return drivers;
    }

    public void setParticipants(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public List<String> getParticipantNames() {
        List<String> participantNames = new ArrayList<>();
        for (Driver driver : drivers) {
            participantNames.add(driver.getName());
        }
        return participantNames;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RacingTeam racingTeam = (RacingTeam) obj;
        return id == racingTeam.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
