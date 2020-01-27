import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

public class User extends Thread {
    final DataInputStream din;
    final DataOutputStream dout;
    final Socket sock;
    Room room;
    String name;
    String received;

    public User(Socket sock, DataInputStream din, DataOutputStream dout)
    {
        this.sock = sock;
        this.din = din;
        this.dout = dout;
    }

    public void sendMessage(String st){
        try{
            if (room!=null){
                ArrayList<User> userlist = room.getRoomusers();
                for(User u:userlist){
                    if(u!=this){
                        u.dout.writeUTF(st);
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRooms(){
        try{
            if(!Server.roomlist.isEmpty()){
                dout.writeUTF("- Here is a room list");
                for(Room r:Server.roomlist){
                    dout.writeUTF("   "+r.getName());
                    ArrayList<User> l = r.getRoomusers();
                    for(User u:l){
                        dout.writeUTF("     - "+u.name);
                    }
                }
            }else{
                dout.writeUTF("- There is no room here. Lets make a room!");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeRoom(){
        try{
            dout.writeUTF("- Please type a room name.");
            String roomname = din.readUTF();
            Room r = containRoom(roomname);
            if(r!=null){
                dout.writeUTF("- You joined a room '"+roomname+"'");
                r.addUser(this);
                room = r;
                sendMessage("- "+name+" joined the room.");
            }else{
                dout.writeUTF("- You made a room '"+roomname+"'");
                room = new Room(roomname,this);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Room containRoom(String roomname){
        for(Room r:Server.roomlist){
            if(r.getName().equals(roomname)){
                return r;
            }
        }
        return null;
    }

    public void askName(){
        String bo=null;

        try{
            dout.writeUTF("- Welcome to the chat room!!");
            dout.writeUTF("- What is your user name?");
            name = din.readUTF();
//            dout.writeUTF("---- Is your user name "+name+"? y/n ----");
//            bo = din.readUTF();
        }catch (IOException e) {
            e.printStackTrace();
        }

//        while(!bo.equals("y")){
//            try{
//                if(bo.equals("n")){
//                    dout.writeUTF("---- Please type again ----");
//                    name = din.readUTF();
//                }
//                dout.writeUTF("---- Is your username '"+name+"'? y/n ----");
//                bo = din.readUTF();
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        try{
            dout.writeUTF("- Welcome "+name+"!!");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(){
        try {
            sendMessage("- "+name+" left the room");
            System.out.println("Client " + this.sock + " sends exit...");
            System.out.println("Closing this connection.");
            this.sock.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        askName();
        showRooms();
        makeRoom();

        while (true)
        {
            try {
                received = din.readUTF();

                if(received.equals("exit")) {
                    exit();
                    break;
                }

                Date date = new Date();
                sendMessage(name+": "+received+"  ["+date+"]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.din.close();
            this.dout.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}