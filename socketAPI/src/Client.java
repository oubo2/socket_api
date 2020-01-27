import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements Runnable, ActionListener {

    public static void main(String[] args){
        Client window = new Client();
        window.setSize(400, 600);
        window.setVisible(true);
    }

    private Thread thread;
    InetAddress ip;
    Socket sock;
    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;

    private static final String APPNAME = "CHAT ROOM";
    private JTextArea msgTextArea;
    private JTextField msgTextField;

    private JButton enterButton;

    public Client(){
        super(APPNAME);

        JPanel topPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        msgTextArea = new JTextArea();
        msgTextField = new JTextField();
        enterButton = new JButton("ENTER");
        enterButton.addActionListener(this);
        enterButton.setActionCommand("enter");

        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(msgTextField, BorderLayout.CENTER);
        bottomPanel.add(enterButton, BorderLayout.EAST);

        msgTextArea.setEditable(false);

        this.getContentPane().add(new JScrollPane(msgTextArea), BorderLayout.CENTER);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(leftPanel, BorderLayout.WEST);
        this.getContentPane().add(rightPanel, BorderLayout.EAST);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            ip = InetAddress.getByName("localhost");
            sock = new Socket(ip, 5056);
            din = new DataInputStream(sock.getInputStream());
            dout = new DataOutputStream(sock.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));

            while (true)
            {
                String tosend;
                if(br.ready()) {
                    tosend=br.readLine();
                    dout.writeUTF(tosend);

                    if (tosend.equals("exit")) {
                        System.out.println("Closing this connection : " + sock);
                        sock.close();
                        System.out.println("Connection closed");
                        break;
                    }
                }

                if(din.available()>0){
                    String st = din.readUTF();
                    msgTextArea.append(st+"\n");
                    System.out.println(st);
                }
            }

            din.close();
            dout.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(cmd.equals("enter")) {
            String ms = msgTextField.getText();
            msgTextArea.append("You: "+ms+"\n");
            sendMessage(ms);
            msgTextField.setText("");
        }
    }

    public void sendMessage(String ms){
        try {
            DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            dout.writeUTF(ms);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}