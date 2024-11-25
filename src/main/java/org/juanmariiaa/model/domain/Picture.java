package org.juanmariiaa.model.domain;

import java.util.Objects;

public class Picture {
    private int id;
    private byte[] imageData;
    private CarRace carRace;

    public Picture() {
        this.id = 0;
        this.imageData = null;
        this.carRace = null;
    }

    public Picture(int id, byte[] imageData) {
        this.id = id;
        this.imageData = imageData;
        this.carRace = carRace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Picture)) return false;
        Picture picture = (Picture) o;
        return id == picture.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                '}';
    }
}
