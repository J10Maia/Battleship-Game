import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class ComputerLogic {

    private final int GRID_SIZE=10;

    public ComputerLogic(){};

    /**
     * Place the random generated ships of the list on the board
     *
     * @param board Board to place the ships on
     * @param ships List of the random generated ships
     */
    public void placeShips(Board board, List<Ship> ships) {
        Random random = new Random();

        for (Ship ship : ships) {
            boolean placed = false;
            while (!placed) {
                int x = random.nextInt(board.getSizeX());
                int y = random.nextInt(board.getSizeY());
                boolean vertical = random.nextBoolean();

                if (canPlaceShip(board, x, y, ship, vertical)) {
                    ship.setVertical(vertical);
                    placeShip(board, ship, x, y);
                    placed = true;
                }
            }
        }
    }

    /**
     * Check if the ship can be placed on the board.
     * Check if in bounds and if there is no other ship
     *
     * @param board Board to check
     * @param x Row of the cell to check
     * @param y Column of the cell to check
     * @param ship Ship to place
     * @param vertical Orientation of the ship
     * @return Boolean true= can be placed
     */
    private boolean canPlaceShip(Board board, int x, int y, Ship ship, boolean vertical) {
        List<int[]> shipCoordinates = new ArrayList<>();
        int size = ship.getSize();

        for (int i = 0; i < size; i++) {
            if (vertical) {
                if (x + i >= board.getSizeX() || board.get_square(x + i, y).get_status() == 's') {
                    return false;
                }
                shipCoordinates.add(new int[]{x + i, y});
            } else {
                if (y + i >= board.getSizeY() || board.get_square(x, y + i).get_status() == 's') {
                    return false;
                }
                shipCoordinates.add(new int[]{x, y + i});
            }
        }

        return true;
    }

    /**
     * Place one ship on the board
     *
     * @param board Board to place the ship on
     * @param ship Ship to place
     * @param x Row of the random cell
     * @param y Column of the random cell
     */


    public void placeShip(Board board, Ship ship, int x, int y) {
        List<int[]> shipCoordinates = new ArrayList<>();
        int size = ship.getSize();
    
        for (int i = 0; i < size; i++) {
            if (ship.isVertical()) {
                board.setSquareNewStatus(x + i, y, 's');
                shipCoordinates.add(new int[]{x + i, y});
            } else {
                board.setSquareNewStatus(x, y + i, 's');
                shipCoordinates.add(new int[]{x, y + i});
            }
        }
    }

    /**
     * Shoot randomly on the player board
     *
     * @param board Board to shoot
     * @param enemyGrid Gridpane to display the shot
     * @param allShips List of all ships
     */
    public void randomShot(Board board, GridPane enemyGrid, List<Ship> allShips){
        Random random = new Random();
        int row;
        int col;
        char status;
        
        do {
            row = random.nextInt(GRID_SIZE);
            col = random.nextInt(GRID_SIZE);
            status = board.getSquareStatus(row, col);
            System.out.println("RANDOM SHOT : row : " + row + " ; col : " + col + " ; status : " + status);
        } while (status == 'x' || status == 'm');

        Node node = GameScene.getNodeByRowColumnIndex(row, col, enemyGrid);
        if (node instanceof ImageView) {
            ImageView imageView = (ImageView) node;
            Image newImage;
            if(status=='s'){
                newImage = new Image("images/HitShip.png");
                board.get_square(row, col).set_status('x'); // Marquer le coup sur la grille
                // Identifier quel bateau a été touché
                for (Ship ship : allShips) {
                    for (int[] coordinate : ship.getCoordinates()) {
                        if (coordinate[0] == row && coordinate[1] == col) {
                            ship.updateLives(); // Enlever une vie au bateau touché
                            if(ship.getLives()==0) System.out.println("Ship sunk");
                            break;
                        }
                    }
                }
            } else {
                newImage = new Image("images/MissedShip.png");
                board.get_square(row, col).set_status('m'); // Marquer le coup sur la grille
            }
            imageView.setImage(newImage); // Mettre à jour l'image de la case touchée
        }
    }

    /**
     * Used to know if all the computer ships are sunk
     *
     * @param board Board to check
     * @return Boolean true=every ships sunk
     */
    
    public static boolean allComputerShipsSunk(Board board) {
        for (int row = 0; row < board.getSizeX(); row++) {
            for (int col = 0; col < board.getSizeY(); col++) {
                if (board.getSquareStatus(row, col) == 's') {
                    return false; // S'il reste au moins une case 's', au moins un bateau n'est pas coulé
                }
            }
        }
        return true; // Toutes les cases 's' ont été touchées, tous les bateaux de l'ordinateur sont coulés
    }
}