import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ServerScene {
    
    ArrayList<Socket> clients = new ArrayList<>();
    
    @FXML
    private Text ConnectText; // text to describe the situation (create or join a server)

    @FXML
    private TextField ServerAddressField;

    @FXML
    private Button btnCreate;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnJoin;

    @FXML
    void btnCreateClicked(ActionEvent event) throws IOException, ClassNotFoundException { // Create a server
        ConnectText.setText("Server creation in progress...");
        //Server server = new Server(5000);
        //Client client1 = new Client("127.0.0.1", 5000);

        Server server = new Server(9090);
        server.start();
        SceneController.getInstance().setPort(9090);
        SceneController.getInstance().setTurn(true);
        SceneController.getInstance().openScene("GameScene.fxml", event);
    }

    @FXML
    void btnHomeClicked(ActionEvent event) throws IOException { // go back home
        SceneController.getInstance().openScene("Menu1.fxml", event);
    }

    @FXML
    void btnJoinClicked(ActionEvent event) throws IOException { // join a server
        ConnectText.setText("Waiting for connection...");
        //Client client2 = new Client("127.0.0.1", 5000);
        SceneController.getInstance().setPort(9091);
        SceneController.getInstance().setTurn(false);
        SceneController.getInstance().openScene("GameScene.fxml", event);


    }

    /*void checkConnection() {
        
        
    }*/

}

