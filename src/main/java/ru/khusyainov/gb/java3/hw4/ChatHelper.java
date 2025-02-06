package ru.khusyainov.gb.java3.hw4;

import ru.khusyainov.gb.java3.hw2.Client;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChatHelper {
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8189;
    public static final String SPACE_REGEX = "\\s";
    private static final String LOGIN_REGEX = "\\b[a-zA-Zа-яА-я0-9!#$%&'()*+,-.:?@^_`]{6,32}\\b";
    private static final String NICK_REGEX = "\\b[a-zA-Zа-яА-я0-9!#$%&'()*+,-.:?@^_`]{5,32}\\b";
    private static final String PASSWORD_REGEX = "\\b[a-zA-Zа-яА-я0-9!#$%&'()*+,-.:?@^_`]{5,64}\\b";
    private static final String MESSAGE_REGEX = ".+";
    private static final String COMMAND_LOGIN = "/auth";
    private static final String AUTHORIZATION_NOT_FULL = "Логин и/или пароль не заполнен(ы)!";
    private static final String AUTHORIZATION_NOT_SENT = "Логин/пароль не отправлены!";
    private static final String LOGIN_COMMAND_REGEX =
            "^" + COMMAND_LOGIN + SPACE_REGEX + LOGIN_REGEX + SPACE_REGEX + PASSWORD_REGEX;
    private static final String AUTHORIZED_STATUS = COMMAND_LOGIN + "ok";
    private static final String AUTHORIZED_STATUS_REGEX = AUTHORIZED_STATUS + SPACE_REGEX + LOGIN_REGEX +
            SPACE_REGEX + NICK_REGEX;
    private static final String COMMAND_CLIENTS_LIST = "/clients";
    private static final String CLIENTS_LIST_REGEX = COMMAND_CLIENTS_LIST + SPACE_REGEX + NICK_REGEX +
            "(" + SPACE_REGEX + NICK_REGEX + ")*";
    private static final String COMMAND_PRIVATE_MESSAGE = "/w ";
    private static final String PRIVATE_MESSAGE_REGEX =
            "^" + COMMAND_PRIVATE_MESSAGE + NICK_REGEX + SPACE_REGEX + MESSAGE_REGEX;
    private static final String MESSAGE_NOT_SENT = "Сообщение не отправлено!";
    public static final String COMMAND_CHANGE_NICK = "/nick ";
    private static final String CHANGE_NICK_REGEX = "^" + COMMAND_CHANGE_NICK + NICK_REGEX;
    public static final String COMMAND_LOGOUT = "/end";
    private static final String SERVER_DISCONNECTED = "Связь с сервером окончена/потеряна.";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final String COLON = ": ";
    private static final String SPACE = " ";
    private static final String MY_NAME = "Я";

    public static String getAuthorizationNotFullMessage() {
        return getDateAndTime() + SPACE + AUTHORIZATION_NOT_FULL;
    }

    public static String getAuthorizationNotSentMessage() {
        return getDateAndTime() + SPACE + AUTHORIZATION_NOT_SENT;
    }

    public static String getLoginCommand(String login, String password) {
        return COMMAND_LOGIN + SPACE + login + SPACE + password;
    }

    public static boolean isAuthorizeCommand(String message) {
        return message != null && message.startsWith(COMMAND_LOGIN);
    }

    private static String[] getPartsIfCommand(String commandRegex, int partsCount, String message) {
        String[] parts = null;
        if (message.matches(commandRegex)) {
            String[] tempParts;
            if (partsCount > 0) {
                tempParts = message.split(SPACE_REGEX, partsCount + 1);
            } else {
                tempParts = message.split(SPACE_REGEX);
                partsCount = tempParts.length - 1;
            }
            parts = new String[partsCount];
            System.arraycopy(tempParts, 1, parts, 0, partsCount);
        }
        return parts;
    }

    public static String[] getPartsIfLoginCommand(String message) {
        return getPartsIfCommand(LOGIN_COMMAND_REGEX, 2, message);
    }

    public static String getAuthorizedStatus(String login, String nick) {
        return AUTHORIZED_STATUS + SPACE + login + SPACE + nick;
    }

    public static Client getClientIfAuthorizedStatus(String message) {
        String[] parts = getPartsIfCommand(AUTHORIZED_STATUS_REGEX, 2, message);
        return parts == null ? null : new Client(parts[0], null, parts[1]);
    }

    public static String getClientsListId() {
        return COMMAND_CLIENTS_LIST;
    }

    public static String[] getClientsIfClientsList(String message) {
        return ChatHelper.getPartsIfCommand(CLIENTS_LIST_REGEX, 0, message);
    }

    public static String getMessageNotSentMessage() {
        return getDateAndTime() + SPACE + MESSAGE_NOT_SENT;
    }

    public static String getPrivateMessageCommand(String toNick) {
        return COMMAND_PRIVATE_MESSAGE + toNick + SPACE;
    }

    public static String[] getPartsIfPrivateMessageCommand(String message) {
        return getPartsIfCommand(PRIVATE_MESSAGE_REGEX, 2, message);
    }

    public static String getMessageToLocalHistory(String message) {
        String[] parts = getPartsIfPrivateMessageCommand(message);
        if (parts == null) {
            return addTimeToMyMessage(message);
        } else {
            return addTimeToMyPrivateMessage(parts[0], parts[1]);
        }
    }

    public static boolean isChangeNickCommand(String message) {
        return message != null && message.startsWith(COMMAND_CHANGE_NICK);
    }

    public static String[] getPartsIfChangeNickCommand(String message) {
        return getPartsIfCommand(CHANGE_NICK_REGEX, 1, message);
    }

    public static boolean isLogoutCommand(String message) {
        return message != null && message.startsWith(COMMAND_LOGOUT);
    }

    public static String getServerDisconnectedMessage() {
        return getDateAndTime() + SPACE + SERVER_DISCONNECTED;
    }

    private static String getDateAndTime() {
        return ZonedDateTime.now().format(DATE_TIME_FORMAT);
    }

    public static String addTime(String message) {
        return getDateAndTime() + COLON + message;
    }

    public static String addTimeToSomeonesMessage(String name, String message) {
        return getDateAndTime() + SPACE + name + COLON + message;
    }

    public static String addTimeToMyMessage(String message) {
        return addTimeToSomeonesMessage(MY_NAME, message);
    }

    public static String addTimeToMyPrivateMessage(String toNick, String message) {
        return addTimeToSomeonesMessage(MY_NAME + SPACE + toNick, message);
    }
}
