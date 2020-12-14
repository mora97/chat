package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.repository.Client;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static AnchorPane root;
    static List<AnchorPane> anchorPanes = new ArrayList<>();
    private static int idCurrentPage = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("view/sample.fxml"));

        anchorPanes.add(FXMLLoader.load(getClass().getResource("view/login.fxml")));
        anchorPanes.add(FXMLLoader.load(getClass().getResource("view/sign_up.fxml")));
        anchorPanes.add(FXMLLoader.load(getClass().getResource("view/main.fxml")));

        root.getChildren().add(anchorPanes.get(0));
        root.getStylesheets().add(getClass().getResource("view/shared.css").toExternalForm());

        primaryStage.setOnCloseRequest(event -> {
            Client.sendMessage("logout");
            System.exit(0);
        });

        primaryStage.setResizable(false);
        primaryStage.setTitle("NIT Messenger");
        primaryStage.setScene(new Scene(root, 650, 550));
        primaryStage.show();
    }

    public static void setPane(int id) {
        root.getChildren().remove(anchorPanes.get(idCurrentPage));
        root.getChildren().add(anchorPanes.get(id));
        idCurrentPage = id;
    }

    public static void setStyle(String styleSheetName) {
        root.getStylesheets().removeAll(Main.class.getResource("view/" + styleSheetName).toExternalForm());
        root.getStylesheets().add(Main.class.getResource("view/" + styleSheetName).toExternalForm());
    }
}
