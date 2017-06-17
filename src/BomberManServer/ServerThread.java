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


                if (clientRequest == null)//cotokurwajest
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
                        serverMessage = "MAP," + server.maps.get(Integer.valueOf(clientCommands[1]) - 1);
                        break;
                    case "SET_HIGHSCORE":
                        String[] temp = server.highScores.split(",");
                        for (int i = 0; i < 10; i++) {
                            if (Integer.valueOf(temp[2 * i + 1]) < Integer.valueOf(clientCommands[2])) {
                                for (int j = i + 1; j < 10; j++) {
                                    temp[2 * j] = temp[2 * j - 2];
                                    temp[2 * j + 1] = temp[2 * j - 1];
                                }
                                temp[2 * i] = clientCommands[1];
                                temp[2 * i + 1] = clientCommands[2];

                                server.highScores = temp[0] + "," + temp[1];
                                for (int j = 1; j < 10; j++)
                                    server.highScores += "," + temp[2 * j] + "," + temp[2 * j + 1];

                                server.highScores += "\n";
                                break;
                            }
                        }
                    default:
                        serverMessage = "SERVER_PANIC 600\n";
                }

                outputStream.writeBytes(serverMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
