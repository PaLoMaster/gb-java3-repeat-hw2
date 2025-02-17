package ru.khusyainov.gb.java3.hw7.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="users")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nick;

    public Client() {
    }

    public Client(int id, String login, String password, String nick) {
        this(login, password, nick);
        this.id = id;
    }

    public Client(String login, String password, String nick) {
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean equals(String nick) {
        if (this.login == null || this.nick == null || this.password == null) return false;
        return this.nick.equals(nick);
    }

    public boolean equals(String login, String password) {
        return Objects.equals(this.login, login) && Objects.equals(this.password, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client other = (Client) o;
        return Objects.equals(login, other.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "Client{" +
                "login='" + login + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
