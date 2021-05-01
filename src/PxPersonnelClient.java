import java.net.*;
import java.io.*;
import java.util.Scanner;

// TCP client to run PxPersonnel in client-server mode
public class PxPersonnelClient {
    public static void main(String[] args) {
        System.out.println("Welcome to PxPersonnel Client");
        try {
            Scanner sc = new Scanner(System.in);
            InetAddress ip = InetAddress.getByName("localhost"); // Change if online
            Socket socket = new Socket(ip, 7733);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                int flag = listener(inputStream);
                if(flag == 5) {
                    String toSend = sc.nextLine();
                    outputStream.println(toSend);
                }
                else if(flag == 4) {
                    continue;
                }
                else if(flag == -1) {
                    System.out.println("Server disconnected, end session");
                    break;
                }
                else if(flag == 0) {
                    break;
                }
            }
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Listen and print inStream from Server
    // End flag 4: end of stream
    // End flag 5: need to reply
    // End flag -1: lost connection from server
    // End flag 0: quit session
    private static int listener(BufferedReader inputStream) throws IOException {
        int s;
        while ((s = inputStream.read()) != 4 && s != 5 && s!= -1 && s!= 0) {
            char c = (char) s;
            System.out.print(c);
        }
        return s;
    }
}
