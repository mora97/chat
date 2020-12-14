package sample.repository;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Server {
    static Vector<ServerThread> activeUser = new Vector<>();
    static int i = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Socket socket;

        System.out.println("server is online.");
        while (true) {
            socket = serverSocket.accept();

            System.out.println("client " + i + "connected to : " + socket);

//            DataInputStream dis = new DataInputStream(socket.getInputStream());
//            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter dos = new PrintWriter(socket.getOutputStream(), true);


            System.out.println("creating a server thread...");

//            String firstMsg = dis.readUTF();
            String firstMsg = dis.readLine();

            System.out.println(firstMsg);

            StringTokenizer tokenizer = new StringTokenizer(firstMsg, ":");
            String id = tokenizer.nextToken();
            ServerThread st = new ServerThread(socket, id, dis, dos);


            activeUser.add(st);
            for (ServerThread user: activeUser) {
                user.dos.println(firstMsg);
            }

            Thread thread = new Thread(st);
            System.out.println("add client to active contacts");
            thread.start();
            i++;
        }
    }


}
