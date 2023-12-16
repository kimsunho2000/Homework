import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

class Server {
    int serverPort = 6000;
    int regPort = 5000;
    ArrayList<Attendee> users = new ArrayList<>();

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
    void sendUserInfo() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet;
        String msg = "";
        byte[] buf;
        for(Attendee u : users){
            msg = msg + "send by sunhokim:" + u.addr + ",";
            msg = msg + u.port + ",";
            msg = msg + u.name + "\n";
        }
        System.out.println("user list:");
        System.out.println();

        buf = msg.getBytes();
        for (Attendee e : users) {
            InetAddress addr = e.addr;
            int port = regPort;
            packet = new DatagramPacket(buf, buf.length, addr, port);
            socket.send(packet);
        }
        socket.close();
    }
    Server() throws IOException, InterruptedException {
        byte[] buffer = new byte[1500];
        System.out.println("Start server");
        DatagramSocket socket = new DatagramSocket(serverPort);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            InetAddress addr = packet.getAddress();
            int port = packet.getPort();
            String name = new String(buffer);
            users.add(new Attendee(addr, port, name));
            System.out.println(new String(buffer));
            System.out.println(addr + " " + port + " " + name);
            Thread.sleep(1);
            sendUserInfo();
        }
    }
}

    public class Main {
        public static void main(String[] args) throws IOException, InterruptedException {
            new Server();
        }
    }