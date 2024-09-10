import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class WelcomeScene {

    private String playerName;

    @FXML
    private TextField NameField;

    @FXML
    private Button btnGo;

    @FXML
    void btnGoClicked(ActionEvent event) throws IOException { // method when GO button is clicked
        
        playerName = NameField.getText(); // Store the text as the player name
        if(playerName!=""){ // if no name, nothing happen
            SceneController.getInstance().setPlayerName(playerName); // store player name in the scene controller class
            NameField.setText(playerName); // display the name
            SceneController.getInstance().openScene("Menu1.fxml", event); // go to the menu scene
        }
        
    }

}
