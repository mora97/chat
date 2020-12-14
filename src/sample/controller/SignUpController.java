package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sample.Main;
import sample.repository.Client;
import sample.repository.Data;
import sample.repository.Person;
import sample.services.CheckAuth;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable, CheckAuth {

    public Label errorNameLabel, errorIDLabel, errorEmailLabel, errorPasswordLabel, errorConfirmPassLabel;
    public TextField nameTF, idTF, emailTF;
    public PasswordField passwordF, confirmPasswordF;
    private Data newUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorNameLabel.setVisible(false);
        errorIDLabel.setVisible(false);
        errorEmailLabel.setVisible(false);
        errorPasswordLabel.setVisible(false);
        errorConfirmPassLabel.setVisible(false);
    }

    public SignUpController() {
        newUser = new Data();
    }

    @Override
    public boolean checkAuth() {
        int flag = 1;

        if (idTF.getText().equals("") || idTF.getText().length() < 6 || !checkCharacter(idTF.getText())) {
            errorIDLabel.setVisible(true);
            flag = 0;
        }

        if (emailTF.getText().equals("")) {
            errorEmailLabel.setText("Please Fill Email Filed");
            errorEmailLabel.setVisible(true);
            flag = 0;
        } else {
            if (!checkEmail()) {
                errorEmailLabel.setText("Email start with www. and finish with .com");
                errorEmailLabel.setVisible(true);
                flag = 0;
            } else {
                errorEmailLabel.setVisible(false);
            }
        }

        if (passwordF.getText().equals("")) {
            errorPasswordLabel.setText("Please Fill Password Filed");
            errorPasswordLabel.setVisible(true);
            flag = 0;
        } else {
            errorPasswordLabel.setVisible(false);
        }

        if (confirmPasswordF.getText().equals("")) {
            errorConfirmPassLabel.setText("Please Fill Confirm Password Filed");
            errorConfirmPassLabel.setVisible(true);
            flag = 0;
        } else {
            errorConfirmPassLabel.setVisible(false);
        }

        if (!confirmPasswordF.getText().equals(passwordF.getText())) {
            errorConfirmPassLabel.setText("Confirmed Password is not matched");
            errorConfirmPassLabel.setVisible(true);
            flag = 0;
        }

        if (flag == 1) {
            return true;
        }

        return false;
    }

    private boolean checkCharacter(String id) {
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (((int)c >= 65 && (int)c <= 90) || ((int)c >= 97 && (int)c <= 122)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public void singUp(ActionEvent event) throws IOException {
        if (checkAuth()) {
            Client client = new Client();
            Person.setLoggedinUserId(idTF.getText());
            Client.sendMessage(Person.getLoggedinUserId()+":newcontact");
            newUser.setData(nameTF.getText(), idTF.getText(), emailTF.getText(), passwordF.getText());
            Main.setPane(2);
        }
    }

    public void Login(ActionEvent event) {
        Main.setPane(0);
    }

    private boolean checkEmail() {
        String email = emailTF.getText();
        String firstPart = email.substring(0, 4);
        String lastPart = email.substring(email.length() - 4, email.length());

        if (firstPart.equals("www.") && lastPart.equals(".com")) {
            return true;
        }

        return false;
    }
}
