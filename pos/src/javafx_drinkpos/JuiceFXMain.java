package javafx_drinkpos;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JuiceFXMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Parent root  = FXMLLoader.load(getClass().getResource("JuiceFXML.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("POS訂單系統!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
