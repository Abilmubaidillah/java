import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileClientTCP {
    public static void main(String[] args) {
        String serverAddress = "localhost"; 
        int port = 12345;

        try (Socket socket = new Socket(serverAddress, port);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(getFilePath())) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            System.out.println("File berhasil dikirim ke server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFilePath() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan path file yang ingin dikirim: ");
        return scanner.nextLine();
    }
}
