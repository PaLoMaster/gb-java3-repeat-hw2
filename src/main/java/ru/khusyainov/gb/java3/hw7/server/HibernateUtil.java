package ru.khusyainov.gb.java3.hw7.server;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.khusyainov.gb.java3.hw7.entity.Client;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static void buildSessionFactory() {
        sessionFactory = new Configuration().addAnnotatedClass(Client.class).buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }
}
