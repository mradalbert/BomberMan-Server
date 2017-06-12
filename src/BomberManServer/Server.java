package BomberManServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Wojtek on 20.05.2017.
 */
public class Server {
    private static int PORT = 42069;

    int levelNo; // Liczba poziomów;
    String gameconfig = ""; // Konfiguracja gry wczytana z pliku;
    ArrayList<String> maps; // Tablica konfiguracji map wczytana z plików.
    String highScores;

    public Server() {
        maps = new ArrayList<>();
    }


    public static void main(final String[] args) throws IOException {
        System.out.println("BomberManServer");
        System.out.println("by Paweł Kulig & Wojciech Sobczak sp. z.o.o.");
        System.out.println("All rights reserved\n");
        new Server().runServer();
    }

    public void runServer() throws IOException {
        loadFiles();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Serwer uruchomiony! Nasłuchiwanie na porcie " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket, this).start();
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas uruchamiania serwera!");
            System.err.println(e);
        }
    }


    public ArrayList<String> findFilesWithExtension(String folder, String extension) {
        // znajduje ścieżkę do folderu projektu
        String absolutePath = new File("").getAbsolutePath();

        ArrayList<String> files = new ArrayList<String>();
        File dir = new File(absolutePath + File.separator + folder);

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(extension))
                files.add(absolutePath + File.separator + "maps" + File.separator + file.getName());
        }
        return files;
    }


    private void loadFiles() {

        ArrayList<String> mapPaths = findFilesWithExtension("maps", ".mapconfig");
        Properties properties = new Properties();

        InputStream input = null;

        for (String path : mapPaths) {
            String mapString = "";
            try {
                input = new FileInputStream(path);
                properties.load(input);

                mapString += properties.getProperty("screenWidth") + ",";
                mapString += properties.getProperty("screenHeight") + ",";
                mapString += properties.getProperty("mapObjects") + ",";
                mapString += properties.getProperty("multibombPositions") + ",";
                mapString += properties.getProperty("superbombPositions") + ",";
                mapString += properties.getProperty("cherryPosition") + ",";
                mapString += properties.getProperty("eliminationPosition") + ",";
                mapString += properties.getProperty("enemyPositions") + ",";
                mapString += properties.getProperty("heroPosition") + "\n";

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            maps.add(mapString);
        }

        levelNo = maps.size();

        String highScoresString = "HIGHSCORES,";

        try {

            input = new FileInputStream("highscores.config");
            properties = new Properties();
            properties.load(input);

            Enumeration<?> names = properties.propertyNames();

            while (names.hasMoreElements()) {
                String key = (String) names.nextElement();
                String value = properties.getProperty(key);

                highScoresString += key + "," + value + ",";
            }

            highScoresString = highScoresString.substring(0, highScoresString.length() - 2);
            highScoresString += "\n";

            highScores = highScoresString;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        try {
            input = new FileInputStream("main.config");
            properties = new Properties();
            properties.load(input);

            gameconfig += properties.getProperty("pixelHeight") + ",";
            gameconfig += properties.getProperty("pixelWidth") + ",";
            gameconfig += properties.getProperty("shockwaveLength") + ",";
            gameconfig += properties.getProperty("bombWaitingTime") + ",";
            gameconfig += properties.getProperty("shockwaveTime") + ",";
            gameconfig += properties.getProperty("multibombDuration") + ",";
            gameconfig += properties.getProperty("panelWidth") + ",";
            gameconfig += properties.getProperty("fps") + ",";
            gameconfig += properties.getProperty("speed") + ",";
            gameconfig += properties.getProperty("shockwaveRecoveryTime") + ",\n";

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
