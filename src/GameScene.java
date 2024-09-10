import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class GameScene {

    private final int GRID_SIZE = 10;
    private boolean inGame;
    private ImageView clickedShip;
    private int shipSize;
    Ship buff; // the buffer ship is used to store the ship currently selected
    private String keyPressed;
    boolean enemyReady = false;

    @FXML
    private GridPane EnemyGrid;

    @FXML
    private GridPane PlayerGrid;

    @FXML
    private ImageView ship1;

    @FXML
    private ImageView ship2;

    @FXML
    private ImageView ship3;

    @FXML
    private ImageView ship4;

    @FXML
    private ImageView ship5;

    @FXML
    private Button btnHome;

    @FXML
    private Text txtInformation;

    @FXML
    private Text usernameTxt;

    List<Ship> allShips;

    Ship shipLength5;
    Ship shipLength4;
    Ship shipLength3;
    Ship shipLength2;
    Ship shipLength1;

    Board playerBoard;
    Board enemyBoard;

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket socket;
    int i = 0;
    int x = 0;

    ComputerLogic computer = new ComputerLogic();

    /**
     * Initialization of the game scene : create all ships and the list,
     * display the player name, create the grids and create an event when R key is pressed
     * @throws IOException
     * @throws UnknownHostException
     */
    public void initialize() throws UnknownHostException, IOException {

        if(SceneController.getInstance().isSoloMode() == false){
        socket = new Socket("localhost", SceneController.getInstance().getPort());

        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());  
        }

        System.out.println(SceneController.getInstance().getTurn());
        shipLength5 = new Ship(5, false);
        shipLength4 = new Ship(4, false);
        shipLength3 = new Ship(3, false);
        shipLength2 = new Ship(2, false);
        shipLength1 = new Ship(1, false);
        // create a list with all the ships to compare their coordinates and avoid collision during placement
        allShips = new ArrayList<>();

        allShips.add(shipLength1);
        allShips.add(shipLength2);
        allShips.add(shipLength3);
        allShips.add(shipLength4);
        allShips.add(shipLength5);

        playerBoard = new Board(GRID_SIZE, GRID_SIZE, allShips); // Create a board with squares with status "w" (water)
        enemyBoard = new Board(GRID_SIZE, GRID_SIZE);

        inGame=false;
        String playerName = SceneController.getInstance().getPlayerName();
        usernameTxt.setText(playerName);
        txtInformation.setText("Select a ship and click on the board to place it. Press R to rotate the selected ship");
        inGame=false; // false while placing the ships and true after the game beginsw
        createBattleGrid();

        Platform.runLater(() -> {
            Scene scene = usernameTxt.getScene();
            scene.setOnKeyPressed(event -> {
                keyPressed = event.getCode().toString();
                if(buff!=null && keyPressed=="R"){ // R for "Rotate" to place a ship vertically (still need to indicate the orientation of the ship on the scene)
                    buff.setVertical(!buff.isVertical()); // toggle the orientation
                    System.out.println("Rotate");
                }
            });
        });
    }
    

    /**
     * Create the boards with image of blue cell and assign a click event on each cell
     */
    private void createBattleGrid() {

        for (int row = 0; row < GRID_SIZE; row++) { // player grid
            for (int col = 0; col < GRID_SIZE; col++) {
                
                // Create the JavaFX Gridpane with Imageview
                ImageView imageView = new ImageView();
                imageView.setFitWidth(38);
                imageView.setFitHeight(38);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
                
                imageView.setImage(new Image("images/blueCell.png"));
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                int currentRow = row;
                int currentCol = col;
            
                    imageView.setOnMouseClicked(event -> { // function when an image is clicked
                        try {
                            handleCellClick(imageView, currentRow, currentCol);
                        } catch (ClassNotFoundException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });

                PlayerGrid.add(imageView, col, row);
            }
        }

        for (int row = 0; row < GRID_SIZE; row++) { // enemy grid
            for (int col = 0; col < GRID_SIZE; col++) {
                
                ImageView imageView = new ImageView();
                imageView.setFitWidth(38);
                imageView.setFitHeight(38);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
                imageView.setImage(new Image("images/blueCell.png"));
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                int currentRow = row;
                int currentCol = col;

                imageView.setOnMouseClicked(event -> {
                    try {
                        handleCellClick(imageView, currentRow, currentCol);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
                EnemyGrid.add(imageView, col, row);
            }
        }
    }

    /**
     * Handle a click on a cell :
     *  - Display the ship on the grid depending on the ship selected if not in game
     *  - Make transparent the image of the placed ship
     *  - if all ships placed, start the game by create a random enemy
     *  - Display the missed or hit ship image
     *  - Handle win or lose
     *
     * @param imageView Cell clicked on
     * @param row Row of the cell
     * @param col Column of the scene
     * @throws IOException
     * @throws ClassNotFoundException
     */

    private void handleCellClick(ImageView imageView, int row, int col) throws IOException, ClassNotFoundException {

        System.out.println("Image clicked at row: " + GridPane.getRowIndex(imageView) + ", column: " + GridPane.getColumnIndex(imageView));

        if(PlayerGrid.getChildren().contains(imageView)){ // if clicked on player grid

            if(!inGame && clickedShip!=null && buff!=null){ // if game has not begun, a ship is selected (image under the grid and associated to a Ship object)
                if(!buff.isVertical() && col+shipSize<GRID_SIZE+1){ // Horizontally
                    buff.deleteCoordinates(); // reset coordinates
                    for(int i=0;i<shipSize;i++){ // is used to simulate the coordinates wanted
                        int testCol=col+i;
                        buff.setCoordinates(row, testCol);
                    }
                    if(buff.getCommonCoordinatesWithOthers(allShips).isEmpty()){ // if theses coordinates do not hit another ship
                        buff.deleteCoordinates(); // reset coordinates
                        for(int i=0;i<shipSize;i++){
                            int rightCol = col + i;
                            buff.setCoordinates(row, rightCol); // assign the coordinates of the ship
                            
                            if (rightCol < GRID_SIZE) { // if it fits in the grid
                                playerBoard.setSquareNewStatus(row, rightCol, 's'); // change the status of the square on the player grid

                                Node node = getNodeByRowColumnIndex(row, rightCol, PlayerGrid); // get the object on the node (cell)
                                if (node instanceof ImageView) { // if it is an image, it is change as a ship part
                                    ImageView rightImageView = (ImageView) node;
                                    Image newRightImage = new Image("images/shipPart.png");
                                    rightImageView.setImage(newRightImage);
                                    System.out.println("Image changed vertical");
                                }
                            }
                        }
                        
                        buff.placedOnGrid(true); // this ship is now placed on the grid
                    }
                }

                else if(buff.isVertical() && row+shipSize<GRID_SIZE+1){ // Vertically (same commentaries but with row instead of column)
                    buff.deleteCoordinates();
                    for(int i=0;i<shipSize;i++){
                        int testRow=row+i;
                        buff.setCoordinates(testRow, col);
                    }
                    System.out.println(buff.getCommonCoordinatesWithOthers(allShips));
                    
                    if(buff.getCommonCoordinatesWithOthers(allShips).isEmpty()){
                        buff.deleteCoordinates(); // reset coordinates
                        
                        for(int i=0;i<shipSize;i++){
                        int bottomRow = row + i;
                        if (bottomRow < GRID_SIZE) {
                            playerBoard.setSquareNewStatus(bottomRow, col, 's'); // change the status of the square on the player grid

                            Node node = getNodeByRowColumnIndex(bottomRow, col, PlayerGrid);
                            if (node instanceof ImageView) {
                                ImageView rightImageView = (ImageView) node;
                                Image newRightImage = new Image("images/shipPart.png");
                                rightImageView.setImage(newRightImage);
                                System.out.println("Image changed horizontal");
                            }
                        }
                    }

                    buff.placedOnGrid(true);
                    }
                }

                // set the image of the ship under the grid transparent so that we understand it is placed
                if(shipLength5.isPlaced())ship5.setOpacity(0.5);
                else ship5.setOpacity(1);
                if(shipLength4.isPlaced())ship4.setOpacity(0.5);
                else ship4.setOpacity(1);
                if(shipLength3.isPlaced())ship3.setOpacity(0.5);
                else ship3.setOpacity(1);
                if(shipLength2.isPlaced())ship2.setOpacity(0.5);
                else ship2.setOpacity(1);
                if(shipLength1.isPlaced())ship1.setOpacity(0.5);
                else ship1.setOpacity(1);
                if(buff.isPlaced()) clickedShip.setOpacity(0.5);
                
                buff.clearCommonCoordinates(); // clear the common coordinates
                buff = null; // clear the buffer ship
                clickedShip = null; // clear the image selected
                playerBoard.printBoard();
                
                if(shipLength1.isPlaced() && shipLength2.isPlaced() && shipLength3.isPlaced() && shipLength4.isPlaced() && shipLength5.isPlaced()){
                    
                    inGame=true;
                    x=1;

                    txtInformation.setText("Click on Enemy grid to shoot");

        //UNTIL HERE IS THE SOME FOR SOLO AND MULTIPLAYER

                    if(SceneController.getInstance().isSoloMode()){ // if SOLO mode played
                        List<Ship> ships = new ArrayList<>();
                        for(int i=1;i<=5;i++){
                            ships.add(new Ship(i, false)); // creation of random ships
                        }

                        computer.placeShips(enemyBoard, ships);

                        enemyBoard.printBoard(); // print the grid to debug
                    }

                    /*if(SceneController.getInstance().isSoloMode() == false){

                        System.out.println("Client > Multiplayer Mode");


                        if(SceneController.getInstance().getTurn()==false && i<1){

                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > I'm going to send the board - 1ºst case") ;
                            Board outputBoard = new Board(10,10);
                            outputBoard = playerBoard;
                            out.writeObject(outputBoard);
                            out.flush();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Board sent 1ºst case");
                            SceneController.getInstance().setTurn(true);

                        }

                        if(SceneController.getInstance().getTurn()==true && i<1){

                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Waiting to receive the enemyboard - 1ºst case");
                            enemyBoard = (Board) in.readObject();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Board received - Printing enemyBoard - 1ºst case");
                            enemyBoard.printBoard();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + SceneController.getInstance().getTurn() + " > I'm going to send the board - 1ºst case") ;
                            i++;
                        }
                    }*/

                    // IF MULTI MODE : SEND THE BOARD TO THE OTHER PLAYER AND RECEIVE HIS AND SET IT AS ENEMY GRID AND ENEMY BOARD
                }
                if(x==1){
                    if(SceneController.getInstance().isSoloMode() == false){

                        System.out.println("Client > Multiplayer Mode");


                        if(SceneController.getInstance().getTurn()==false && i<1){

                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > I'm going to send the board - 1ºst case") ;
                            Board outputBoard = new Board(10,10);
                            outputBoard = playerBoard;
                            out.writeObject(outputBoard);
                            out.flush();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Board sent 1ºst case");
                            SceneController.getInstance().setTurn(true);

                        }

                        if(SceneController.getInstance().getTurn()==true && i<1){

                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Waiting to receive the enemyboard - 1ºst case");
                            enemyBoard = (Board) in.readObject();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Board received - Printing enemyBoard - 1ºst case");
                            enemyBoard.printBoard();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + SceneController.getInstance().getTurn() + " > I'm going to send the board - 1ºst case") ;
                            i++;
                        }
                    }
                }
            }
        }

        else if(inGame && EnemyGrid.getChildren().contains(imageView)){

            System.out.println("Enemy grid clicked");

            char status = enemyBoard.getSquareStatus(row, col);
            
            System.out.println(status);

            if(status!='x' && status!='m'){

                Node node = getNodeByRowColumnIndex(row, col, EnemyGrid);

                if (node instanceof ImageView) {
                    ImageView ImageView = (ImageView) node;
                    Image newImage;

                    if(status=='s'){
                        newImage = new Image("images/HitShip.png");
                        enemyBoard.setSquareNewStatus(row, col, 'x');
                        ImageView.setImage(newImage);
                    }
                    else{
                        newImage = new Image("images/MissedShip.png");
                        enemyBoard.setSquareNewStatus(row, col, 'm');
                        ImageView.setImage(newImage);
                    }

                    ImageView.setImage(newImage);

                    System.out.println("Image changed");
                }

                    if(SceneController.getInstance().isSoloMode() == true){

                        computer.randomShot(playerBoard, PlayerGrid, allShips);
                        
                        if(ComputerLogic.allComputerShipsSunk(enemyBoard)){
                            System.out.println("YOU WIN");
                            txtInformation.setText("Congratulations, you win the game!");
                            displayGameOverMessage(true);
                            //initialize(); // CREER UNE FONCTION RESETGAME QUI REMET TOUS LES BATEAUX EN MODE NON PLACES, INGAME A 0... (vérifier état de base)
                        // FAIRE APPARAITRE UN TEXTE ET UN BOUTON QUI DIT SI JE VEUX REJOUER OU PAS ET SE SERVIR DE L'EVENT POUR OPENSCENE(GAMESCENE)
                        }
                    }
                    
                    if(SceneController.getInstance().isSoloMode() == false){

                        if(SceneController.getInstance().getTurn()==true){
                        SceneController.getInstance().setTurn(false);
                        }

                        if(SceneController.getInstance().getTurn()==false){

                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > I'm going to send the board - 2ºnd case im already false") ;
                            Board outputBoard = new Board(10,10);
                            outputBoard = playerBoard;
                            out.writeObject(outputBoard);
                            out.flush();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Board sent - 2ºnd case");
                            SceneController.getInstance().setTurn(true);

                        }

                        if(SceneController.getInstance().getTurn()==true){
                            
                            //in = new ObjectInputStream(socketin.getInputStream());
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Waiting to receive the enemyboard - 2ºnd case");
                            //enemyBoard = (Board) in.readObject();
                            in.readObject();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + " > Board received - Printing enemyBoard - 2ºnd case");
                            enemyBoard.printBoard();
                            System.out.println("Client - "+ SceneController.getInstance().getPort() + SceneController.getInstance().getTurn() + " > I'm going to send the board - 2ºnd case Im still true") ;
                        }

                        if(enemyBoard.isgameover()){
                            
                            System.out.println("YOU WIN");
                            txtInformation.setText("Congratulations, you win the game!");
                            displayGameOverMessage(true);
                            socket.close();
                                //initialize(); // CREER UNE FONCTION RESETGAME QUI REMET TOUS LES BATEAUX EN MODE NON PLACES, INGAME A 0... (vérifier état de base)
                        }

                    }
                    
                    if(isGameOver()){
                        System.out.println("YOU LOOSE, GAME OVER");
                        txtInformation.setText("Game over, Enemy wins!");
                        displayGameOverMessage(false);
                    }

                //}
            }

            else System.out.println("Cell already clicked");
        }
    }

    /*public void receiveBoard(){
        new Thread(() -> {
            try {
                enemyBoard = (Board) in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }

    public void sendBoard(){
        new Thread(() -> {
            try {
                Board outputBoard = new Board(10,10);
                outputBoard = playerBoard;
                out.writeObject(outputBoard);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }*/

    /**
     * Used to get the image on the clicked cell of the grid
     *
     * @param row Row clicked
     * @param column Column clicked
     * @param gridPane Gridpane clicked
     * @return Image selected
     */
    public static ImageView getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            if (node instanceof ImageView && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return (ImageView) node;
            }
        }
        return null;
    }

    /**
     * Open the Menu scene
     *
     * @param event Event to get the current stage
     * @throws IOException
     */
    @FXML
    void btnHomeClicked(ActionEvent event) throws IOException {
        SceneController.getInstance().openScene("Menu1.fxml", event);
    }

    /**
     * Used when the image of a ship is clicked under the grid : ship the player wants to place
     * Associate the image of the ship with the ship object
     *
     * @param event Event to get the image clicked
     */
    @FXML
    void shipClicked(MouseEvent event) {
        
        clickedShip = (ImageView) event.getSource(); // get the image clicked
        // then associate the image with the Ship object if the ship is not already placed and highlight it
        if(clickedShip == ship5 && !shipLength5.isPlaced()){
            buff = shipLength5;
            shipSize=shipLength5.getSize();
            highlightShip(clickedShip);
        }
        else if(clickedShip == ship4 && !shipLength4.isPlaced()){
            buff = shipLength4;
            shipSize=shipLength4.getSize();
            highlightShip(clickedShip);
        }
        else if(clickedShip == ship3 && !shipLength3.isPlaced()){
            buff = shipLength3;
            shipSize=shipLength3.getSize();
            highlightShip(clickedShip);
        }
        else if(clickedShip == ship2 && !shipLength2.isPlaced()){
            buff = shipLength2;
            shipSize=shipLength2.getSize();
            highlightShip(clickedShip);
        }
        else if(clickedShip == ship1 && !shipLength1.isPlaced()){
            buff = shipLength1;
            shipSize=shipLength1.getSize();
            highlightShip(clickedShip);
        }
    }

    /**
     * Used to change opacity of the ships image when a ship is selected
     *
     * @param selectedShip Image of the clicked ship image
     */
    private void highlightShip(ImageView selectedShip){
        ship5.setOpacity(0.5);
        ship4.setOpacity(0.5);
        ship3.setOpacity(0.5);
        ship2.setOpacity(0.5);
        ship1.setOpacity(0.5);
        clickedShip.setOpacity(1);
    }

    /**
     * Check if all player ships are sunk
     *
     * @return Boolean true=all ships sunk
     */
    private boolean isGameOver(){
        if(shipLength1.getLives()==0 && shipLength2.getLives()==0 && shipLength3.getLives()==0 && shipLength4.getLives()==0 && shipLength5.getLives()==0){
            return true;
        }
        else return false;
    }
    
    /**
     * Display a pop up after the end of the game showing victory or defeat with a replay button and a quit button
     *
     * @param playerWins Boolean true=player win
     */
    private void displayGameOverMessage(boolean playerWins) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT); // Supprime la barre de titre de la fenêtre

        VBox popupContent = new VBox(10);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        Label messageLabel = new Label(playerWins ? "You win!" : "You loose!");
        messageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button replayButton = new Button("Play again");
        Button quitButton = new Button("Quit");

        replayButton.setOnAction(e -> {
            try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) btnHome.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    } catch (IOException e1) {
        e1.printStackTrace();
    }
            popupStage.close();

        });

        quitButton.setOnAction(e -> {
            try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu1.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) btnHome.getScene().getWindow(); // Modifier ici en fonction de votre configuration actuelle
        stage.setScene(scene);
        stage.show();
    } catch (IOException e1) {
        e1.printStackTrace(); // ou gestion de l'erreur
    }
            popupStage.close(); // Ferme la boîte de dialogue
        });

        popupContent.getChildren().addAll(messageLabel, replayButton, quitButton);

        BorderPane root = new BorderPane();
        root.setCenter(popupContent);

        Scene popupScene = new Scene(root, 300, 200); // Taille de la boîte de dialogue
        popupScene.setFill(Color.TRANSPARENT); // Rend la scène transparente

        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }
}
