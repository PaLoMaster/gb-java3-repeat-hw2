package ru.khusyainov.gb.java3.hw7.repository;

import org.hibernate.Session;
import ru.khusyainov.gb.java3.hw7.entity.Client;
import ru.khusyainov.gb.java3.hw7.server.HibernateUtil;

import java.util.List;

public class ClientRepository {
    public static void insert(Client client) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        session.persist(client);
        session.getTransaction().commit();
    }

    public static void update(Client client) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        session.merge(client);
        session.getTransaction().commit();
    }

    public static void delete(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        Client client = session.get(Client.class, id);
        session.remove(client);
        session.getTransaction().commit();
    }

    public static Client get(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        Client client = session.get(Client.class, id);
        session.getTransaction().commit();
        return client;
    }

    public static Client get(String login) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        Client client = session.createQuery("WHERE login = :login", Client.class)
                .setParameter("login", login).getSingleResultOrNull();
        session.getTransaction().commit();
        return client;
    }

    public static Client get(String login, String password) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        Client client = session.createQuery("WHERE login = :login and password = :password", Client.class)
                .setParameter("login", login).setParameter("password", password).getSingleResultOrNull();
        session.getTransaction().commit();
        return client;
    }

    public static List<Client> get() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
        List<Client> clients = session.createQuery("FROM users", Client.class).list();
        session.getTransaction().commit();
        return clients;
    }
}
