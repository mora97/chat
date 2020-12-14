package sample.repository;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ServerThread implements Runnable {

    Scanner scan = new Scanner(System.in);
    private String id;
    BufferedReader dis;
    PrintWriter dos;
    Socket socket;
    boolean isLoggedin = true;

    public ServerThread(Socket socket, String id, BufferedReader dis, PrintWriter dos) {
        this.socket = socket;
        this.id = id;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;

        while (true) {
            try {
                boolean isGame = false;
                received = dis.readLine();
                System.out.println("server msg:" + received);
                if (received.equals("logout")) {
                    this.isLoggedin = false;
                    this.socket.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, ":");
                String msgToSend = st.nextToken();
                String recipient = st.nextToken();
                String fullMessage = this.id + ":" + msgToSend;
                String point = "";
                if (st.hasMoreTokens()) {
                    String finalScore = st.nextToken();
                    point = st.nextToken();
                    fullMessage += ":" + finalScore + ":" + point;
                    isGame = true;
                }

                System.out.println("rc: " + recipient + "--- full:" + fullMessage);
                for (ServerThread user : Server.activeUser) {
                    if (user.id.equals(recipient) && user.isLoggedin == true) {
                        Data data = new Data();
                        System.out.println("ST");
                        user.dos.println(fullMessage);
                        if (isGame) {
                            if (msgToSend.equals("newgame")) {
                                data.clearFileData(this.id, recipient);
                                data.clearFileData(recipient, this.id);
                            } else {
                                String cell = findCell(msgToSend);
                                data.wirteGameResult(this.id, recipient, cell, point);
                            }
                        } else {
                            System.out.println("chat");
                            data.writeChats(this.id, msgToSend, recipient);
                        }
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            this.dis.close();
            this.dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findCell(String msgToSend) {
        StringTokenizer st = new StringTokenizer(msgToSend, ":");
        String cell = st.nextToken();

        return cell;
    }


}
