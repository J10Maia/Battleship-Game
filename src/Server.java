import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server{
    ServerSocket socketOut, s0, s1;
    ServerSocket socketIn;
    ArrayList<Socket> clients = new ArrayList<>(0);
    ArrayList<ObjectOutputStream> outputs = new ArrayList<>();
    ArrayList<ObjectInputStream> inputs = new ArrayList<>();
    ArrayList<Board> boards = new ArrayList<>();
    int player = 0;

    public Server(int port) throws ClassNotFoundException {
        try {

            s0 = new ServerSocket(9090);
            s1 = new ServerSocket(9091);

            System.out.println("Server is listening on port " + port + "...");
            acceptClients(); 
            System.out.println("SERVER > Players connected");
            //start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClients() {

        System.out.println("SERVER > Waiting for players");
        
        new Thread(()-> {

            while (clients.size()<2) {

                try {

                    Socket socket0 = s0.accept();
                    System.out.println("Boas! player 1");
                    clients.add(socket0); 
                    Socket socket1 = s1.accept();
                    System.out.println("Boas! player 2");
                    clients.add(socket1);
                    ObjectOutputStream out0 = new ObjectOutputStream(socket0.getOutputStream());
                    outputs.add(out0);
                    ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
                    outputs.add(out1);
                    ObjectInputStream in0 = new ObjectInputStream(socket0.getInputStream());
                    inputs.add(in0);
                    ObjectInputStream in1 = new ObjectInputStream(socket1.getInputStream());
                    inputs.add(in1);
                    boards.add(new Board(10, 10));
                    boards.add(new Board(10, 10));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void broadcast(Board board) throws IOException {               //Sends Info to all Clients

        Board board2 = new Board(board);

        for (ObjectOutputStream out : outputs) {
            out.writeObject(board2);
            //out.flush();
        }
    }

    public void sendGame(int id, Board board) throws IOException {

        System.out.println("SERVER > Sending player " + (-id+1) + " board:");
        //board.printBoard();

        Board board2 = new Board(board);

        board2 = board;

        //System.out.println("SERVER > Printing board2");

        //board2.printBoard();

        outputs.get(id).writeObject(board2);
        outputs.get(id).flush();
         
        System.out.println("SERVER > Game sent to player " + id);
    }

    public boolean getBoard(int player) throws IOException, ClassNotFoundException { //player is the receiving player

        try{
        
            Board receivedBoard = (Board) inputs.get(-player+1).readObject();
            System.out.println("Server > Printing received board");
            receivedBoard.printBoard();
            System.out.println("SERVER > Board received");
            boards.set(-player+1, receivedBoard);
            return true;

        } catch (IOException e) {
            System.out.println("SERVER > Player " + (-player+1) + " disconnected");
            return false;
        }
     
    }

    public void getShots() throws IOException, ClassNotFoundException {

        System.out.println("SERVER > Playing");

        getBoard(player);     //player is the receiving player

        sendGame(player, boards.get(-player+1));

        player = -player+1;

    }

    public void start() throws ClassNotFoundException, IOException {

        new Thread(() -> {

            try {

                while (clients.size()<2){
                    Thread.sleep(1000);
                }

                System.out.println("SERVER > Starting game");
                System.out.println("SERVER > Player " + player + "receives");

                getBoard(player);
                sendGame(player, boards.get(-player+1));
                player = -player+1;

                while(getBoard(player)){
                    
                sendGame(player, boards.get(-player+1));
                player = -player+1;

                }
                

             } catch (ClassNotFoundException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /*public static void main(String[] args) throws IOException {
        int port = 9090;
        Server server = new Server(port);
        server.start();
    }*/
}



