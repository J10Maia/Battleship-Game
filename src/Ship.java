import java.util.ArrayList;
import java.util.List;

public class Ship {
    private int size;
    private boolean vertical;
    private int lives;
    private boolean placedShip;
    private List<int[]> shipCoordinate; // list of the coordinate of the ship {row, col}
    List<int[]> commonCoordinates = new ArrayList<>(); // list of the common coordinates between all the ships

    /**
     * Constructor used to create a new ship
     *
     * @param size Size of the ship on the grid
     * @param vertical Orientation of the ship on the grid (default : false=horizontal)
     */
    public Ship(int size, boolean vertical) {
        this.size = size;
        this.vertical = vertical;
        placedShip = false;
        shipCoordinate = new ArrayList<>();
        this.lives=size;
    }

    /**
     * Used to get the size of a ship
     *
     * @return Size of the ship
     */
    public int getSize(){
        return size;
    }

    /**
     * Set a boolean that describes if the ship is placed on the grid
     *
     * @param isPlaced Boolean : true=placed
     */
    public void placedOnGrid(boolean isPlaced){
        this.placedShip = isPlaced;
    }

    /**
     * Getter of ship placed
     *
     * @return Boolean true=placed
     */
    public boolean isPlaced(){
        return placedShip;
    }

    /**
     * Getter of ship orientation
     *
     * @return Ship orientation true= vertical
     */
    public boolean isVertical() {
        return vertical;
    }

    /**
     * Set ship orientation
     *
     * @param vertical boolean true=vertical
     */
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    /**
     * Set the coordinates of a ship part and add them to a list
     * This method has to be used in a for() depending on the size and th orientation of the boat
     *
     * @param row Row between 0 and 9
     * @param col Column between 0 and 9
     */
    public void setCoordinates(int row, int col){
        int[] coordinate = {row, col};
        shipCoordinate.add(coordinate);
    }

    /**
     * Clear the coordinates list of ship
     */
    public void deleteCoordinates(){
        shipCoordinate.clear();
    }

    /**
     * @return The list of coordinates
     */
    public List<int[]> getCoordinates(){
        return shipCoordinate;
    }

    /**
     * Used to get the common coordinates in order to place the ships avoiding collision
     *
     * @param allShips the list of all the ships
     * @return A list of all the common coordinates between all the ships
     */
    public List<int[]> getCommonCoordinatesWithOthers(List<Ship> allShips) {
        
        for (Ship otherShip : allShips) {
            if (otherShip != this) {
                List<int[]> otherShipCoordinates = otherShip.getCoordinates();

                for (int[] coord1 : shipCoordinate) {
                    for (int[] coord2 : otherShipCoordinates) {
                        if (coord1[0] == coord2[0] && coord1[1] == coord2[1]) {
                            commonCoordinates.add(coord1);
                            break;
                        }
                    }
                }
            }
        }
        return commonCoordinates;
    }

    /**
     * Used to clear all the common coordinates
     */
    public void clearCommonCoordinates() {
        commonCoordinates = new ArrayList<>();
    }


    /**
     * Add the coordinates of the ship to the list
     *
     * @param x Row
     * @param y Column
     */
    public void addCoordinate(int x, int y) {
        int[] coordinate = {x, y};
        shipCoordinate.add(coordinate);
    }

    /**
     * Remove a life to the ship when hit
     */
    public void updateLives(){
        this.lives= this.lives - 1;
    }

    /**
     * @return number of lives
     */
    public int getLives(){
        return this.lives;
    }
}
