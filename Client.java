package try2;

import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;

public class Client {
    public static void main(String arg, int port) throws IOException {
        
        Socket socket = new Socket(arg, port);
        Scanner in = new Scanner(socket.getInputStream());
        System.out.println("Server response: " + in.nextLine());
    }
}