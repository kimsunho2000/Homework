package ConferenceClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class Source extends JFrame {
    JTextArea inWindow;
    JTextField outWindow;
    ArrayList<Attendee> users = new ArrayList<>();
    int recvPort = 4000;
    int regPort = 5000;
    InetAddress serverAddr = InetAddress.getByName("220.69.218.211");
    int serverPort = 6000;

    class Attendee {
        InetAddress addr;
        int port;
        String name;

        Attendee(InetAddress addr, int port, String name) {
            this.addr = addr;
            this.port = port;
            this.name = new String(name);
        }
    }
    void makeupUserData(String msg) throws UnknownHostException {
        String [] userData = new String[3];
        for(int i = 0 ; i < 3; i++) {
            userData[i] = Arrays.toString(msg.split(","));
            users.add(new Attendee(InetAddress.getByName(userData[0]),Integer.parseInt(userData[1]), userData[3]));
        }
    }
    void register() throws IOException {
        DatagramPacket packet;
        byte[] buf;
        String s = "KSH";
        buf = s.getBytes();
        packet = new DatagramPacket(buf, buf.length, serverAddr, serverPort);
        DatagramSocket socket = new DatagramSocket();
        recvPort = socket.getLocalPort();
        socket.send(packet);
        socket.close();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    byte[] buffer = new byte[1500];
                    DatagramSocket socket = null;
                    try {
                        socket = new DatagramSocket(regPort);
                    } catch (SocketException e) {
                        throw new RuntimeException(e);
                    }
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        try {
                            socket.receive(packet);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Register reply");
                        String msg = new String(buffer);
                        System.out.println(msg);
                        try {
                            makeupUserData(msg);
                        } catch (UnknownHostException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }
        }}).start();
    }

    void multipleSend(String msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet;
        byte[] buf = msg.getBytes();
        for (Attendee e : users) {
            InetAddress addr = e.addr;
            int port = e.port;
            packet = new DatagramPacket(buf, buf.length, addr, port);
            socket.send(packet);
        }
        socket.close();
    }

    Source() throws IOException {
        setTitle("Client");
        inWindow = new JTextArea(20, 30);
        add(inWindow);
        outWindow = new JTextField(40);
        outWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = outWindow.getText().trim();
                System.out.println("Client send:" + msg);
                outWindow.setText("");
                try {
                    multipleSend(msg);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                msg = "(kimsunho)" + msg;
                inWindow.setText(inWindow.getText() + "\n" + msg);

            }
        });
        outWindow.setBackground(Color.YELLOW);
        add(outWindow, BorderLayout.SOUTH);
        setLocation(500, 0);
        pack();
        setVisible(true);
        register();
//        users.add(new Attendee(InetAddress.getByName("220.69.218.211"), recvPort, "OYY"));
//        users.add(new Attendee(InetAddress.getByName("220.69.218.220"), recvPort, "JSM"));
//        users.add(new Attendee(InetAddress.getByName("220.69.218.227"), recvPort, "LHZ"));
        //users.add(new Attendee(InetAddress.getByName("220.69.218.235"), recvPort,"KSH"));
//        users.add(new Attendee(InetAddress.getByName("220.69.218.219"), recvPort, "PSH"));
//        users.add(new Attendee(InetAddress.getByName("220.69.218.229"), recvPort, "AJY"));
//        users.add(new Attendee(InetAddress.getByName("220.69.218.224"), recvPort, "KDH"));

        byte[] buffer = new byte[1500];
        DatagramSocket socket = new DatagramSocket(recvPort);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String msg = new String(buffer);
            System.out.println(msg);
            inWindow.setText(inWindow.getText() + "\n" + msg);
        }
    }
}

class CClient {
    public static void main(String[] args) throws IOException {
        new Source();
    }
}