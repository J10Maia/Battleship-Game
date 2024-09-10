import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    private static SceneController instance;
    private String playerName;
    private static boolean soloMode=false;
    private static boolean myturn;
    private int port;

    /**
     * Empty constructor
     */
    private SceneController(){}

    /**
     * Used to get a unique instance of scene controller
     *
     * @return a unique instance
     */
    public static SceneController getInstance() {
        if (instance == null) {
            instance = new SceneController();
        }
        return instance;
    }

    /**
     * Used to get the player name
     *
     * @return the player name as a string
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Used to set the player name
     *
     * @param name Player name as a String
     */
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    /**
     * Used to open a new scene based on the current stage
     *
     * @param sceneName Name of the scene to open
     * @param event Event used to get the current stage
     * @throws IOException
     */
    public void openScene(String sceneName, ActionEvent event) throws IOException { // method to open another scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Used to set the game as solo or not
     *
     * @param bool True=solo ; false=multiplayer
     */
    public static void setSoloMode(boolean bool){
        soloMode=bool;
    }

    public void setTurn(boolean bool){
        myturn=bool;
    }

    public boolean getTurn(){
        return myturn;
    }

    /**
     * Used to get the game mode
     *
     * @return Boolean true if solo
     */
    public boolean isSoloMode(){
        return soloMode;
    }

    public void setPort(int port){
        this.port=port;
    }

    public int getPort(){
        return port;
    }
}
