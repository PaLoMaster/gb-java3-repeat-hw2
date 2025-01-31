package ru.khusyainov.gb.java3.hw2;

import java.util.Objects;

public class Client {
    private String login;
    private String password;
    private String nick;

    public Client(String login, String password, String nick) {
        this.login = login;
        this.password = password;
        this.nick = nick;
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
        if (this.login == null || this.password == null) return false;
        return this.login.equals(login) && this.password.equals(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return login.equals(client.login);
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
