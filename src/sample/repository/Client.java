package sample.repository;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import sample.controller.MainController;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client {
    private final static int SERVER_PORT = 1234;
    private Data data;
    private static BufferedReader dis;
    private static PrintWriter dos;
    private InetAddress ip;
    private Socket socket;
    private String id;
    private static ListView listView, contactListV;
    private static Button[][] button;
    private static Label loggedinScoreLabel, rivalScoreLabel;

    public Client() {
        try {
            ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, SERVER_PORT);
            dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dos = new PrintWriter(socket.getOutputStream(), true);
            data = new Data();
            readThread();
        } catch (IOException e) {
            System.out.println("server is not run");
            return;
        }
    }

    public static void setContactListView(ListView ls) {
        contactListV = ls;
    }

    public static void setListview(ListView ls) {
        listView = ls;
    }

    public static void setBtn(Button[][] btn) {
        button = btn;
    }

    public static void setLoggedinScoreLabel(Label label) {
        loggedinScoreLabel = label;
    }

    public static void setRivalScoreLabel(Label label) {
        rivalScoreLabel = label;
    }

    public static void sendMessage(String msg) {
        System.out.println(msg);
        dos.println(msg);
    }

    public void readThread() {
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String msg = dis.readLine();
                        System.out.println("rcv mess: " +msg);

                        StringTokenizer st = new StringTokenizer(msg, ":");
                        String firstPart = st.nextToken();
                        String secondPart = st.nextToken();

                        //for add new contact to list
                        if (secondPart.equals("newcontact")) {
                            data.createFile(firstPart);
                            updateList();
                            continue;
                        }

                        if (secondPart.equals("oldcontact")) {
                            updateList();
                            continue;
                        }

                        //for game msg header
                        if (secondPart.equals("newgame")) {
                            Platform.runLater(() -> {
                                rivalScoreLabel.setText("0");
                                loggedinScoreLabel.setText("0");
                            });
                            renewBoardCells();
                            continue;
                        }

                        if (st.hasMoreTokens()) {
                            String thirdPart = st.nextToken();
                            String fourthPart = st.nextToken();
                            System.out.println("point" + fourthPart);
                            String[] cells = secondPart.split(",");
                            System.out.println(cells[0] + " - " + cells[1]);
                            int cell_x = Integer.parseInt(cells[0]);
                            int cell_y = Integer.parseInt(cells[1]);
                            MainController.setTurn(Person.getLoggedinUserId());
                            MainController.addSelectedCell();
                            Platform.runLater(() -> {
                                button[cell_x][cell_y].setText(fourthPart);
                                button[cell_x][cell_y].setStyle("-fx-background-color: #e942ff");
                                rivalScoreLabel.setText(thirdPart);
                            });
                        } else {//for chat msg header
                            System.out.println("client chat");
                            Platform.runLater(() -> {
                                listView.getItems().add(msg);
                            });
                        }

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        });
        readMessage.start();
    }

    private void updateList() throws FileNotFoundException, InterruptedException {
        System.out.println("test");
        Thread.sleep(200);

        List<Person> contacts = new ArrayList<>();
        ObservableList contactList = FXCollections.observableArrayList();

        Platform.runLater(() -> {
            contactListV.getItems().clear();
        });

        contacts.removeAll(contacts);
        contacts = data.readData();
        contactList.removeAll(contactList);
        for (Person contact : contacts) {
            if (!contact.id.equals(Person.getLoggedinUserId()))
                contactList.add(contact.id);
        }

        Platform.runLater(() -> {
            contactListV.getItems().addAll(contactList);
        });
    }

    private void renewBoardCells() {
        MainController.setTurn(Person.getLoggedinUserId());
        Platform.runLater(() -> {
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < 12; j++) {
                    button[i][j].setText("");
                    button[i][j].setStyle(null);
                }
            }
        });
    }
}
