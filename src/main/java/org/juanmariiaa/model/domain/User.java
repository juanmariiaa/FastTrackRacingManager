package org.juanmariiaa.model.domain;

public class User extends Person {
    private int id;
    private String user;
    private String password;

    public User() {
        super();
        this.id = 0;
        this.user = "";
        this.password = "";
    }

    public User(int id, String dni, String user) {
        super(dni, "", "");
        this.id = id;
        this.user = user;
        this.password = "";
    }

    public User(int id, String dni, String name, String surname, String user, String password) {
        super(dni, name, surname);
        this.id = id;
        this.user = user;
        this.password = password;
    }

    public User(String dni, String name, String surname, String user, String password) {
        super(dni, name, surname);
        this.user = user;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", " + super.toString() + ", user=" + user + "]";
    }
}
