import java.util.ArrayList;

public class Room {
    private String roomname;
    private User hostuser;
    private ArrayList<User> roomusers;

    public Room(String roomname, User hostuser){
        roomusers = new ArrayList<User>();
        this.roomname = roomname;
        this.hostuser = hostuser;
        addUser(hostuser);
        Server.roomlist.add(this);
    }

    public String getName() {
        return roomname;
    }

    public User getHostUser() {
        return hostuser;
    }

    public ArrayList<User> getRoomusers() {
        return roomusers;
    }

    public boolean containsUser(User u) {
        return roomusers.contains(u);
    }

    public void addUser(User user) {
        roomusers.add(user);
    }
}
