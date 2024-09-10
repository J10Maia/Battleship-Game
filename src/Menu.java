import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;



public class Menu {

    public String username;

    @FXML
    private Button btnMulti;

    @FXML
    private Button btnSolo;

    @FXML
    private Button btnStats;

    @FXML
    private Text nameText;

    /**
     * Get the player name in the scene controller and display it on the scene
     */
    public void initialize() {
        String playerName = SceneController.getInstance().getPlayerName();
        nameText.setText(playerName);
    }

    /**
     * Handle actions after the Multiplayer button is clicked
     *
     * @param event Event associated to the button that is used to get the current scene
     * @throws IOException
     */
    @FXML
    void btnMultiClicked(ActionEvent event) throws IOException {
        System.out.println("Multi clicked");
        SceneController.getInstance().openScene("ServerScene.fxml", event);
    }

    /**
     * Handle actions after the Solo button is clicked
     *
     * @param event Event associated to the button that is used to get the current scene
     * @throws IOException
     */
    @FXML
    void btnSoloClicked(ActionEvent event) throws IOException { // method when Solo button clicked
        System.out.println("Solo clicked");
        SceneController.setSoloMode(true);
        SceneController.getInstance().openScene("GameScene.fxml", event);
    }

    @FXML
    void btnStatsClicked(ActionEvent event) {

    }
}