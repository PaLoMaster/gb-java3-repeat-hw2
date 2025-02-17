package ru.khusyainov.gb.java3.hw4.server;

import ru.khusyainov.gb.java3.hw2.server.AuthService;
import ru.khusyainov.gb.java3.hw2.server.SQLiteAuthService;
import ru.khusyainov.gb.java3.hw4.ChatHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    private static final String CLIENT_CONNECTED_NOT_AUTHORIZED = "Клиент подключился, ждём авторизации.";
    private List<ClientHandler> clients;
    private AuthService authService;
    private ServerSocket serverSocket;
    private ExecutorService service;

    public MyServer() {
        try {
            serverSocket = new ServerSocket(ChatHelper.SERVER_PORT);
            authService = new SQLiteAuthService();
            authService.start();
            clients = new LinkedList<>();
            int count = 5;
            service = Executors.newFixedThreadPool(count);
            for (int i = 0; i < count; i++) {
                service.execute(this::connectionWait);
            }
        } catch (IOException e) {
            System.err.println(ChatHelper.getExceptionString(e));
        }
    }

    private void connectionWait() {
        System.out.println(ChatHelper.addTime("Ждём подключения новых клиентов..."));
        Socket socket;
        try {
            socket = serverSocket.accept();
            System.out.println(ChatHelper.addTime(CLIENT_CONNECTED_NOT_AUTHORIZED));
            new ClientHandler(this, socket);
        } catch (IOException e) {
            System.err.println(ChatHelper.getExceptionString(e));
        }
    }

    public ExecutorService getExecutorService() {
        return service;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isClientOnline(String nick) {
        return clients.stream().anyMatch(client -> client.equals(nick));
    }

    public synchronized void sendMessageToClient(ClientHandler fromClient, String message, String toClientNick) {
        for (ClientHandler client : clients) {
            if (client.equals(toClientNick)) {
                client.sendMessageToClient(
                        ChatHelper.addTimeToSomeonesMessage(fromClient.getNick() + " (личное)", message));
                return;
            }
        }
        fromClient.sendMessageToClient(ChatHelper.addTimeToSomeonesMessage(toClientNick,
                "Клиент не в сети. Сообщение не доставлено: " + message));
    }

    public synchronized void sendMessageToClients(ClientHandler fromClient, String message) {
        String messageWithTime = ChatHelper.addTimeToSomeonesMessage(fromClient.getNick(), message);
        clients.stream().filter(client -> !client.equals(fromClient))
                .forEach(client -> client.sendMessageToClient(messageWithTime));
    }

    public synchronized void subscribe(ClientHandler client) {
        if (client.getNick() != null) {
            clients.add(client);
            broadcastClientsList();
            final String CLIENT_CONNECTED = "Клиент подключился.";
            sendMessageToClients(client, CLIENT_CONNECTED);
            System.out.println(ChatHelper.addTimeToSomeonesMessage(client.getNick(), CLIENT_CONNECTED));
        } else {
            System.err.println(ChatHelper.addTime("Ошибка в работе сервера!? " + CLIENT_CONNECTED_NOT_AUTHORIZED));
        }
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
        if (client.getNick() != null) {
            broadcastClientsList();
            final String CLIENT_DISCONNECTED = "Клиент отключился.";
            sendMessageToClients(client, CLIENT_DISCONNECTED);
            System.out.println(ChatHelper.addTimeToSomeonesMessage(client.getNick(), CLIENT_DISCONNECTED));
        } else {
            System.out.println(ChatHelper.addTime("Клиент отключился без авторизации."));
        }
        if (clients.isEmpty()) {
            System.out.println(ChatHelper.addTime("Все клиенты отключились."));
        }
        service.execute(this::connectionWait);
    }

    public synchronized void broadcastClientsList() {
        StringBuilder clientsList = new StringBuilder(ChatHelper.getClientsListId());
        for (ClientHandler client : clients) {
            clientsList.append(" ").append(client.getNick());
        }
        clients.forEach(client -> client.sendMessageToClient(clientsList.toString()));
    }
}
