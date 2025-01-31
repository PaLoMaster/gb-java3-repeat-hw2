package ru.khusyainov.gb.java3.hw3.server;

import ru.khusyainov.gb.java3.hw2.server.AuthService;
import ru.khusyainov.gb.java3.hw2.server.SQLiteAuthService;
import ru.khusyainov.gb.java3.hw3.ChatHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class MyServer {
    private static final String CLIENT_CONNECTED_NOT_AUTHORIZED = "Клиент подключился, ждём авторизации.";
    private final String SERVER_ERROR = "Ошибка в работе сервера";
    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer() {
        try (ServerSocket serverSocket = new ServerSocket(ChatHelper.SERVER_PORT)) {
            authService = new SQLiteAuthService();
            authService.start();
            clients = new LinkedList<>();
            while (true) {
                System.out.println(ChatHelper.addTime("Ждём подключения новых клиентов..."));
                Socket socket = serverSocket.accept();
                System.out.println(ChatHelper.addTime(CLIENT_CONNECTED_NOT_AUTHORIZED));
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.err.println(ChatHelper.addTime(SERVER_ERROR + ":1 " + e));
        }
        if (authService != null) {
            authService.stop();
        }
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
            System.out.println(ChatHelper.addTime(SERVER_ERROR + "!? " + CLIENT_CONNECTED_NOT_AUTHORIZED));
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
    }

    public synchronized void broadcastClientsList() {
        StringBuilder clientsList = new StringBuilder(ChatHelper.getClientsListId());
        for (ClientHandler client : clients) {
            clientsList.append(" ").append(client.getNick());
        }
        clients.forEach(client -> client.sendMessageToClient(clientsList.toString()));
    }
}
