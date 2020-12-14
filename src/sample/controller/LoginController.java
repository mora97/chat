package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sample.Main;
import sample.repository.Client;
import sample.repository.Data;
import sample.repository.Person;
import sample.services.CheckAuth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable, CheckAuth {
    public TextField idTF;
    public PasswordField passwrodF;
    public Label errorLabel;

    private List<Person> users;
    private Data usersData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setVisible(false);
    }

    public LoginController() throws FileNotFoundException {
        usersData = new Data();
        users = usersData.readData();
        for (int i = 0; i < users.size(); i++) {
            System.out.println("i & p: " + users.get(i).id + " - " + users.get(i).password);
        }
    }

    public boolean checkAuth() {
        if (idTF.getText().equals("") || passwrodF.getText().equals("")) {
            errorLabel.setText("Please Fill ID and Password");
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
            System.out.println(idTF.getText());
            System.out.println(passwrodF.getText());
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id == null && users.get(i).password == null) {
                    continue;
                }
                if (users.get(i).id.equals(idTF.getText()) && users.get(i).password.equals(passwrodF.getText())) {
                    System.out.println("login successfully");
                    return true;
                }
            }
        }

        return false;
    }

    public void login() throws IOException {
        if (checkAuth()) {
            Person.setLoggedinUserId(idTF.getText());
            Client client = new Client();
            Client.sendMessage(Person.getLoggedinUserId() + ":oldcontact");
            Main.setPane(2);
        } else {
            errorLabel.setText("you are not Registered");
            errorLabel.setVisible(true);
        }
    }


    public void sign_up(ActionEvent event) {
        Main.setPane(1);
    }
}
