package ru.khusyainov.gb.java3.hw2.server;

import ru.khusyainov.gb.java3.hw2.Client;

public interface AuthService {
    void start();
    Client getClientByLoginPassword(String login, String password);
    void stop();
}
