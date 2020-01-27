import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server {

    static ArrayList<User> userlist = new ArrayList<>();
    static ArrayList<Room> roomlist = new ArrayList<>();
    static ServerSocket socket;

    public boolean containRoom(String roomname){
        for(Room r:roomlist){
            if(r.getName().equals(roomname)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        socket = new ServerSocket(5056);

        while (true)
        {
            Socket client = null;

            try {
                client = socket.accept();

                System.out.println("A new client is connected : " + client);

                // obtaining input and out streams
                DataInputStream din = new DataInputStream(client.getInputStream());
                DataOutputStream dout = new DataOutputStream(client.getOutputStream());
                //threadList.add(dout);

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                User thred = new User(client, din, dout);
                userlist.add(thred);
                thred.start();

            }
            catch (Exception e){
                client.close();
                e.printStackTrace();
            }
        }
    }

}