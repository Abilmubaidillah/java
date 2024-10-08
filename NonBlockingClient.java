import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NonBlockingClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5678;

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));
        clientChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Connected to the server. Type your message:");

        new Thread(() -> {
            try {
                while (true) {
                    int bytesRead = clientChannel.read(buffer);
                    if (bytesRead > 0) {
                        String message = new String(buffer.array()).trim();
                        System.out.println("Server: " + message);
                        buffer.clear();
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        }).start();

        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            buffer.put(message.getBytes());
            buffer.flip();
            clientChannel.write(buffer);
            buffer.clear();
        }

        clientChannel.close();
    }
}
