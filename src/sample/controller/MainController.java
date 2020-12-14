package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import sample.Main;
import sample.repository.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

public class MainController implements Initializable {
    public ImageView settingImgV, chatImgV, gameImgV, logoutImgV;
    public Pane chatPane, gamePane;
    public ListView<String> contactListV, displayMessageListV;
    public TextField writeMessageTF, searchTF;
    public Button sendBtn;
    public GridPane gameBoard;
    public Button[][] gameCells;
    public Label loggedInLabel, loggedInScoreLabel, winnerLabel;
    public Label rivalLabel, rivalScoreLabel;
    public VBox settingPane, contactPane;
    public Pane alertPane;

    private ObservableList contactList = FXCollections.observableArrayList();
    private ObservableList<String> chatList = FXCollections.observableArrayList();
    private ObservableList<String> gameResultList = FXCollections.observableArrayList();
    private Data data = new Data();
    private List<Person> contacts = new ArrayList<>();
    private List<String> chats = new ArrayList<>();
    private List<String> gameResult = new ArrayList<>();
    private String selectedContactId = "";
    private String writtenMessage = "";
    private int clickedCell_X = -1, clickedCell_Y = -1, finalScore = 0, rivalScore = 0;
    private static int selectedCell = 0;
    private static String turn = "";
    private String styleSheetName = "shared.css";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        turn = Person.getLoggedinUserId();
        String baseUrl = "src/assets/";
        sendBtn.setVisible(false);
        searchTF.setPromptText("search ID");
        writeMessageTF.setPromptText("write message hear...");
        writeMessageTF.setVisible(false);
        chatPane.setVisible(true);
        gamePane.setVisible(false);
        settingPane.setVisible(false);
        alertPane.setVisible(false);
        displayMessageListV.getItems().add("nothing");
        displayMessageListV.setVisible(false);
        rivalLabel.setText("");
        rivalScoreLabel.setText("0");

        try {
            loadContacts();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        initGameBoard();
        Client.setContactListView(contactListV);
        Client.setListview(displayMessageListV);
        Client.setRivalScoreLabel(rivalScoreLabel);
        Client.setLoggedinScoreLabel(loggedInScoreLabel);

        settingImgV.setImage(new Image(new File(baseUrl + "setting.png").toURI().toString()));
        chatImgV.setImage(new Image(new File(baseUrl + "chat.png").toURI().toString()));
        gameImgV.setImage(new Image(new File(baseUrl + "game.png").toURI().toString()));
        logoutImgV.setImage(new Image(new File(baseUrl + "logout.png").toURI().toString()));

        //add listener to imageviews
        settingImgV.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                settingPane.setVisible(true);
                gamePane.setVisible(false);
                chatPane.setVisible(false);
                contactPane.setVisible(false);
                mouseEvent.consume();
            }
        });
        chatImgV.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("chat image clicked");
                chatPane.setVisible(true);
                contactPane.setVisible(true);
                gamePane.setVisible(false);
                settingPane.setVisible(false);
                mouseEvent.consume();
            }
        });

        gameImgV.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("game image clicked");
                gamePane.setVisible(true);
                contactPane.setVisible(true);
                chatPane.setVisible(false);
                settingPane.setVisible(false);
                loggedInLabel.setText(Person.getLoggedinUserId());
                loggedInScoreLabel.setText(finalScore + "");
                mouseEvent.consume();
            }
        });
        logoutImgV.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                Client.sendMessage("logout");
                System.exit(0);
                mouseEvent.consume();
            }
        });

        searchTF.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal, Object newVal) {
                if (searchTF.getText().equals("")) {
                    try {
                        loadContacts();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initGameBoard() {
        gameCells = new Button[12][12];
        selectedCell = 0;
        turn = Person.getLoggedinUserId();

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                gameCells[i][j] = new Button();
                gameCells[i][j].setText("");
                gameCells[i][j].setId(i + "," + j);
                gameCells[i][j].setMinWidth(30);
                gameCells[i][j].setMaxHeight(60);
                gameCells[i][j].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (turn.equals(Person.getLoggedinUserId()) && selectedCell != 144) {
                            String btnId = ((Control) event.getSource()).getId();
                            String[] splitBtnId = btnId.split(",");
                            clickedCell_X = Integer.parseInt(splitBtnId[0]);
                            clickedCell_Y = Integer.parseInt(splitBtnId[1]);

                            if (gameCells[clickedCell_X][clickedCell_Y].getText().equals("")) {
                                selectedCell++;
                                int randNum = (int) (Math.random() * (7)) - 2;
                                fillCell(randNum, clickedCell_X, clickedCell_Y);
                            }
                            turn = "";
                        } else {
                            if (selectedCell == 144) {
                                System.out.println("game is finished");
                                if (rivalScore > finalScore) {
                                    winnerLabel.setText("You Loos");
                                    alertPane.setVisible(true);
                                } else {
                                    winnerLabel.setText("You Win");
                                    alertPane.setVisible(true);
                                }

                                try {
                                    data.clearFileData(Person.getLoggedinUserId(), selectedContactId);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                winnerLabel.setText("Wait for your rival...");
                                alertPane.setVisible(true);
                            }
                        }
                    }

                    private void fillCell(int randNum, int cell_X, int cell_Y) {
                        String btnId = cell_X + "," + cell_Y;
                        gameCells[cell_X][cell_Y].setText(randNum + "");
                        gameCells[cell_X][cell_Y].setStyle("-fx-background-color: #1cffbc");
                        score(randNum, false);
                        Client.sendMessage(btnId + ":" + selectedContactId + ":" + finalScore + ":" + randNum);
                    }
                });

                gameBoard.add(gameCells[i][j], i, j);
            }
        }
        Client.setBtn(gameCells);
    }

    public void score(int point, boolean isRival) {
        if (point < 0) {
            if (isRival) {
                rivalScore -= 10;
                rivalScoreLabel.setText(rivalScore + "");
            } else {
                finalScore -= 10;
                loggedInScoreLabel.setText(finalScore + "");
            }
        } else {
            if (isRival) {
                rivalScore += point;
                rivalScoreLabel.setText(rivalScore + "");
            } else {
                finalScore += point;
                loggedInScoreLabel.setText(finalScore + "");
            }
        }
    }

    public void logout() {
        sendBtn.setVisible(false);
        writeMessageTF.setVisible(false);
        settingPane.setVisible(false);
        chatPane.setVisible(true);
        contactPane.setVisible(true);
        gamePane.setVisible(false);
        Main.setPane(0);
    }

    private void loadContacts() throws FileNotFoundException {
        contactList.removeAll(contactList);
        contacts.removeAll(contacts);
        contacts = data.readData();

        for (int i = 0; i < contacts.size(); i++) {
            if (!contacts.get(i).id.equals(Person.getLoggedinUserId())) {
                contactList.add(contacts.get(i).id);
            }
        }

        contactListV.getItems().clear();
        contactListV.getItems().addAll(contactList);
    }

    public void displaySelectedUser(MouseEvent mouseEvent) {
        writeMessageTF.setVisible(true);
        sendBtn.setVisible(true);
        String contact = contactListV.getSelectionModel().getSelectedItem();
        rivalLabel.setText(contact);
        if (contact == null || contact.isEmpty()) {
            displayMessageListV.setVisible(false);
            System.out.println("nothing selected");
        } else {
            selectedContactId = contact;
            getChatHistory();
            getGamesHistory();
        }
    }

    public void getChatHistory() {
        chatList.removeAll(chatList);
        chats.removeAll(chats);
        chats = data.readChats(selectedContactId);
        for (int i = 0; i < chats.size(); i++) {
            chatList.add(chats.get(i));
        }

        System.out.println("selected " + selectedContactId);
        displayMessageListV.getItems().clear();
        displayMessageListV.getItems().addAll(chatList);
        displayMessageListV.setVisible(true);
    }

    public void getGamesHistory() {
        initGameBoard();
        finalScore = 0;
        rivalScore = 0;
        gameResultList.removeAll(gameResultList);
        gameResult.removeAll(gameResult);
        gameResult = data.readGameResult(selectedContactId);

        for (int i = 0; i < gameResult.size(); i++) {
            if (gameResult.get(i).equals("")) {
                continue;
            }

            String[] detail = gameResult.get(i).split(":");
            String[] splitScoreLocation = detail[1].split("=");
            String[] location = splitScoreLocation[0].split(",");
            int cell_X = Integer.parseInt(location[0]);
            int cell_Y = Integer.parseInt(location[1]);

            if (detail[0].equals(Person.getLoggedinUserId())) {
                turn = "";
                score(Integer.parseInt(splitScoreLocation[1]), false);
                gameCells[cell_X][cell_Y].setStyle("-fx-background-color: #e942ff");
            } else {
                turn = Person.getLoggedinUserId();
                score(Integer.parseInt(splitScoreLocation[1]), true);
                gameCells[cell_X][cell_Y].setStyle("-fx-background-color: #1cffbc");
            }

            gameCells[cell_X][cell_Y].setText(splitScoreLocation[1]);
            selectedCell++;
        }

        if (gameResult.size() == 0 || gameResult.size() == 144) {
            turn = Person.getLoggedinUserId();
            loggedInScoreLabel.setText("0");
            rivalScoreLabel.setText("0");
        }
    }

    public void sendMessage() throws IOException {
        writtenMessage = writeMessageTF.getText();
        Client.sendMessage(writtenMessage + ":" + selectedContactId);
        displayMessageListV.getItems().add(Person.getLoggedinUserId() + ": " + writtenMessage);
        writeMessageTF.setText("");
    }

    public void search(ActionEvent event) {
        System.out.println("search:");

        int flag = 0;
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).equals(searchTF.getText())) {
                contactListV.getItems().clear();
                contactListV.getItems().add(searchTF.getText());
                flag = 1;
                break;
            }
        }

        if (flag == 0 && !searchTF.equals("")) {
            contactListV.getItems().clear();
            contactListV.getItems().add("Not found");
        }
    }

    public static void setTurn(String rivalId) {
        turn = rivalId;
    }

    public static void addSelectedCell() {
        selectedCell++;
    }

    public void changeTheme(ActionEvent event) {
        if (styleSheetName.equals("shared.css")) {
            styleSheetName = "change_theme.css";
        } else {
            styleSheetName = "shared.css";
        }

        Main.setStyle(styleSheetName);
    }

    public void close(ActionEvent event) {
        alertPane.setVisible(false);
    }

    public void newGame(ActionEvent event) throws IOException {
        finalScore = 0;
        loggedInScoreLabel.setText("0");
        rivalScoreLabel.setText("0");
        Client.sendMessage("newgame" + ":" + selectedContactId + ":" + 0 + ":" + 0);
        initGameBoard();
    }
}
