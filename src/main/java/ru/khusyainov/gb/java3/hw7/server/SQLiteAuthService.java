package ru.khusyainov.gb.java3.hw7.server;

import ru.khusyainov.gb.java3.hw7.entity.Client;
import ru.khusyainov.gb.java3.hw7.repository.ClientRepository;

public class SQLiteAuthService implements AuthService {

    @Override
    public void start() {
        HibernateUtil.buildSessionFactory();
    }

    @Override
    public Client getClientByLoginPassword(String login, String password) {
        return ClientRepository.get(login, password);
    }

    @Override
    public boolean changeNick(String login, String nick) {
        Client client = ClientRepository.get(login);
        if (client == null || nick == null) {
            return false;
        }
        client.setNick(nick);
        ClientRepository.update(client);
        client = ClientRepository.get(login);
        return client.getNick().equals(nick);
    }

    @Override
    public void stop() {
        HibernateUtil.closeSessionFactory();
    }
}
