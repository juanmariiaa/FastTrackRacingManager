package org.juanmariiaa.model.domain;

import org.juanmariiaa.model.enums.Gender;
import org.juanmariiaa.model.enums.Role;

import java.util.Objects;

public class Driver extends Person {
    private int age;
    private Role role;
    private Gender gender;
    private RacingTeam racingTeam;

    public Driver() {
        super();
        this.age = 0;
        this.role = null;
        this.gender = null;
        this.racingTeam = null;
    }

    public Driver(String dni, String name, String surname, int age, Role role, Gender gender) {
        super(dni, name, surname);
        this.age = age;
        this.role = role;
        this.gender = gender;
        this.racingTeam = racingTeam;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public RacingTeam getTeam() {
        return racingTeam;
    }

    public void setTeam(RacingTeam racingTeam) {
        this.racingTeam = racingTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), age, role, gender, racingTeam);
    }

    @Override
    public String toString() {
        return super.getName();
    }
}
