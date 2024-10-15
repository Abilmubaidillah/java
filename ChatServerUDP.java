import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ChatServerUDP {
    public static void main(String[] args) {
        int port = 12345;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Server UDP berjalan di port " + port);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); 
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Pesan dari " + packet.getAddress() + ":" + packet.getPort() + " - " + message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
