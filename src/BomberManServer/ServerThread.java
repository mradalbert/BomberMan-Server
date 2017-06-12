package BomberManServer;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 * Created by Wojtek on 20.05.2017.
 */

public class ServerThread extends Thread {

    private Socket socket; // Socket;
    private String serverMessage; // Wiadomość wysyłana przez serwer.
    private Server server;

    ServerThread(Socket socket, Server server) {
        System.out.println("Nawiązano płączenie " + socket.getInetAddress());
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            while (true) {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                String clientRequest = input.readLine();
                if(clientRequest == null)
                    break;
                String[] clientCommands = clientRequest.split(",");
                System.out.println(socket.getInetAddress() + " " + clientRequest);
                switch (clientCommands[0]) {
                    case "GET_INFO":
                        serverMessage = "INFO," + String.valueOf(server.levelNo) + "\n";
                        break;
                    case "GET_HIGHSCORES":
                        serverMessage = "HIGHSCORES," + server.highScores;
                        break;
                    case "GET_GAMECONFIG":
                        serverMessage = "GAMECONFIG," + server.gameconfig;
                        break;
                    case "GET_MAP":
                        serverMessage = "MAP," + server.maps.get(Integer.valueOf(clientCommands[1])-1);
                        break;
                    case "SET_HIGHSCORE":
                }

                outputStream.writeBytes(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
