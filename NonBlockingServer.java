import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class NonBlockingServer {
    private static final int PORT = 1234;
    private Selector selector;
    private Map<SocketChannel, String> clientMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new NonBlockingServer().start();
    }

    public void start() throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Non-blocking server started on port " + PORT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    acceptConnection(serverSocketChannel);
                } else if (key.isReadable()) {
                    readMessage(key);
                }
            }
        }
    }

    private void acceptConnection(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        clientMap.put(clientChannel, clientChannel.getRemoteAddress().toString());
        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientMap.remove(clientChannel);
            clientChannel.close();
            System.out.println("Client disconnected.");
            return;
        }

        String message = new String(buffer.array()).trim();
        System.out.println("Received: " + message + " from " + clientMap.get(clientChannel));
        broadcastMessage(message, clientChannel);
    }

    private void broadcastMessage(String message, SocketChannel sender) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
        for (SocketChannel client : clientMap.keySet()) {
            if (client != sender) {
                client.write(buffer);
                buffer.rewind();
            }
        }
    }
}
