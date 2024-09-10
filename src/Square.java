import java.io.Serializable;

public class Square implements Serializable {

    private int x;
    private int y;
    private char status; //can be "m", "x", "w", "s"   (miss, hit, water, ship)
    
    public Square(int x, int y, char status) {
        this.x = x;
        this.y = y;
        this.status = status;
    }

    public int get_X() {
        return this.x;
    }

    public int get_Y() {
        return this.y;
    }

    public char get_status() {
        return this.status;
    }

    public void set_X(int x) {
        this.x = x;
    }
    
    public void set_Y(int y) {
        this.y = y;
    }

    public void set_status(char status) {
        this.status = status;
    }


}

