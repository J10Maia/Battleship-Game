import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.concurrent.TimeUnit;
 
public class App extends Application {
    @Override
    public void start(Stage primaryStage) { // This method is invoked when the JavaFX application starts
  
  
  Parent root; // Parent root will store the root node of the scene graph
try {
    root = FXMLLoader.load(getClass().getResource("WelcomeScene.fxml")); // Load the FXML file (WelcomeScene.fxml) and assign it to the root node
    Scene scene = new Scene(root); // Create a new scene with the root node

    primaryStage.setTitle("Battleship"); // Set the title of the window
    primaryStage.setScene(scene); // Set the scene "WelcomeScene" for the primary stage
    primaryStage.show();} // Display the primary stage
catch (IOException e) { // Does nothing in case of exception
}

}
 
 public static void main(String[] args) {

        launch(args); // Launch JavaFX application
    }
}