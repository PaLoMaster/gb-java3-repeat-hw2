package ru.khusyainov.gb.java3.hw3.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ru.khusyainov.gb.java3.hw2.Client;
import ru.khusyainov.gb.java3.hw3.ChatHelper;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ChatController implements Initializable {
    @FXML
    private HBox loginPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea history;
    @FXML
    private HBox messagePanel;
    @FXML
    private TextField message;
    @FXML
    public ListView<String> clientsListView;
    private Socket socket;
    private Scanner fromServerIn;
    private PrintStream toServerOut;
    private Thread fromServerThread;
    private ObservableList<String> clientsList;
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAuthorized(false);
        Runtime.getRuntime().addShutdownHook(new Thread(this::serverDisconnect));
        clientsList = FXCollections.observableArrayList();
        clientsListView.setItems(clientsList);
        clientsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            if (client != null && client.getNick().equals(item)) {
                                setText(item + " (Я)");
                            } else {
                                setText(item);
                            }
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        loginField.requestFocus();
    }

    private void serverConnect() throws IOException {
        if (socket == null || !socket.isConnected()) {
            socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
            fromServerIn = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            toServerOut = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            fromServerThread = new Thread(() -> {
                try {
                    String fromServerMessage;
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        fromServerMessage = fromServerIn.nextLine();
                        client = ChatHelper.getClientIfAuthorizedStatus(fromServerMessage);
                        if (client != null) {
                            setAuthorized(true);
                            break;
                        }
                        history.appendText(fromServerMessage + "\n");
                    }
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        fromServerMessage = fromServerIn.nextLine();
                        Client newClient = ChatHelper.getClientIfAuthorizedStatus(fromServerMessage);
                        String[] clientsList = ChatHelper.getClientsIfClientsList(fromServerMessage);
                        if (newClient != null) {
                            client = newClient;
                            saveHistory(fromServerMessage);
                        } else if (clientsList != null) {
                            Platform.runLater(() -> this.clientsList.setAll(clientsList));
                        } else {
                            saveHistory(fromServerMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                history.appendText(ChatHelper.getServerDisconnectedMessage());
                if (client != null) {
                    setAuthorized(false);
                }
                serverDisconnect();
            });
            fromServerThread.setDaemon(true);
            fromServerThread.start();
        }
    }

    private void loadHistory() {
        String path = "history_" + client.getLogin() + ".txt";
        BufferedInputStream allHistory = null;
        try {
            if (new File(path).exists()) {
                allHistory = new BufferedInputStream(new FileInputStream(path));
                StringBuilder part = new StringBuilder(new String(allHistory.readAllBytes()));
                if (!part.isEmpty()) {
                    final int LOAD_ROWS = 100;
                    int i = part.length();
                    for (int j = 0; i > 0 && j < LOAD_ROWS; j++) {
                        i = part.substring(0, i).lastIndexOf("\n");
                    }
                    if (i == -1) {
                        i = 0;
                    }
                    history.setText(part.substring(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (allHistory != null) {
                try {
                    allHistory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        history.setScrollTop(Double.MAX_VALUE); //https://bugs.openjdk.org/browse/JDK-8189732
    }

    private void saveHistory(String message) {
        message += "\n";
        history.appendText(message);
        PrintStream allHistory = null;
        try {
            allHistory = new PrintStream(new FileOutputStream("history_" + client.getLogin() + ".txt",
                    true));
            allHistory.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (allHistory != null) {
                allHistory.flush();
                allHistory.close();
            }
        }
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showErrorMessage(ChatHelper.getAuthorizationNotFullMessage());
            return;
        }
        try {
            serverConnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        toServerOut.println(ChatHelper.getLoginCommand(loginField.getText(), passwordField.getText()));
        if (!toServerOut.checkError()) {
            history.appendText(ChatHelper.addTimeToMyMessage("Отправлен запрос авторизации " + loginField.getText() + "\n"));
            loginField.clear();
            passwordField.clear();
            loginField.requestFocus();
        } else {
            showErrorMessage(ChatHelper.getAuthorizationNotSentMessage());
        }
    }

    private void setAuthorized(boolean authorized) {
        if (authorized) {
            Platform.runLater(this::loadHistory);
        } else {
            Platform.runLater(history::clear);
        }
        loginPanel.setVisible(!authorized);
        loginPanel.setManaged(!authorized);
        messagePanel.setVisible(authorized);
        messagePanel.setManaged(authorized);
        clientsListView.setVisible(authorized);
        clientsListView.setManaged(authorized);
        if (!authorized && client != null) {
            client = null;
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String toServerMessage = message.getText();
        toServerOut.println(toServerMessage);
        if (!toServerOut.checkError()) {
            history.appendText(ChatHelper.getMessageToLocalHistory(toServerMessage + "\n"));
            message.clear();
            message.requestFocus();
        } else {
            showErrorMessage(ChatHelper.getMessageNotSentMessage());
        }
    }

    private void showErrorMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(message);
            alert.showAndWait();
        });
    }

    @FXML
    public void clientListViewClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            message.setText(ChatHelper.getPrivateMessageCommand(clientsListView.getSelectionModel().getSelectedItem()));
            message.requestFocus();
            message.selectEnd();
        }
    }

    public void serverDisconnect() {
        if (fromServerThread != null) {
            fromServerThread.interrupt();
            fromServerThread = null;
        }
        if (toServerOut != null) {
            toServerOut.close();
            toServerOut = null;
        }
        if (fromServerIn != null) {
            fromServerIn.close();
            fromServerIn = null;
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}