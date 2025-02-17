package ru.khusyainov.gb.java3.hw7.server;

import ru.khusyainov.gb.java3.hw7.entity.Client;

public interface AuthService {
    void start();
    Client getClientByLoginPassword(String login, String password);
    void stop();
}
