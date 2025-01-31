package ru.khusyainov.gb.java3.hw2.server;

import ru.khusyainov.gb.java3.hw2.Client;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private final List<Client> CLIENTS;

    public BaseAuthService() {
        CLIENTS = new ArrayList<>();
        CLIENTS.add(new Client("login1", "pass1", "nick1"));
        CLIENTS.add(new Client("login2", "pass2", "nick2"));
        CLIENTS.add(new Client("login3", "pass3", "nick3"));
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public Client getClientByLoginPassword(String login, String password) {
        return CLIENTS.stream().filter(client -> client.equals(login, password)).findAny().orElse(null);
    }
}
