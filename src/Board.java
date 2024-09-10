import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    private int sizeX;
    private int sizeY;
    private Square grid[][];
    private int n_ships = 0;
    List<Ship> ships;
    private boolean isGameOver = false;

    /**
     * Constructor with size of the board
     *
     * @param sizeX number of column
     * @param sizeY number of row
     */
    public Board(int sizeX, int sizeY, List ships){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.grid = new Square[sizeX][sizeY];
        for(int row=0; row<sizeX; row++){
            for(int column=0; column<sizeY; column++){
                this.grid[row][column] = new Square(row, column, 'w');
            }
        }
        this.ships = new ArrayList<>();
    }

    public Board(int sizeX, int sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.grid = new Square[sizeX][sizeY];
        for(int row=0; row<sizeX; row++){
            for(int column=0; column<sizeY; column++){
                this.grid[row][column] = new Square(row, column, 'w');
            }
        }
    }

    /**
     * @param board
     */
    public Board(Board board){
        this.sizeX = board.sizeX;
        this.sizeY = board.sizeY;
        this.grid = new Square[sizeX][sizeY];
        for(int row=0; row<sizeX; row++){
            for(int column=0; column<sizeY; column++){
                this.grid[row][column] = board.grid[row][column];
            }
        }
        this.n_ships = board.n_ships;
    }

    /**
     * Print the board on the terminal to debug
     */
    public void printBoard() {
        for (int row = 0; row < sizeX; row++) {
            for (int column = 0; column < sizeY; column++) {
                System.out.print(grid[row][column].get_status() + " ");
            }
            System.out.println();
        }
    }

    public int getSizeX() {
        return this.sizeX;
    }

    public int getSizeY() {
        return this.sizeY;
    }

    public Square[][] get_grid(){
        return this.grid;
    }

    public Square get_square(int x, int y) {
        return this.grid[x][y];
    }

    public int get_num_ships(){
        return this.n_ships;
    }

    public void set_square(Square square) {
        this.grid[square.get_X()][square.get_Y()] = square;
    }

    public void setSquareNewStatus(int row, int col, char NewStatus){
        this.grid[row][col].set_status(NewStatus);
    }

    public char getSquareStatus(int row, int col){
        return this.grid[row][col].get_status();
    }

    public boolean isgameover() {

        for (int row = 0; row < sizeX; row++) {
            for (int column = 0; column < sizeY; column++) {
                if (grid[row][column].get_status() == 's'){
                    return false;
                }
            }
        }
        return true;
    }
}