package es.udc.redes.webserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class WebServer{

    static Properties prop = new Properties();

    public static final HashMap<String,Integer> requestedRes = new HashMap<>();

    public static void PrintPetitions(String res, int port2, String[] string) {
        requestedRes.merge(res, 1, Integer::sum);
        System.out.println(string[0] +" " + res + " | port: " + port2 + " " + requestedRes.get(res));
    }

    public static void main(String[] argv) throws IOException {
        Socket socket = null;
        ServerSocket serverSocket;
        prop.load(new FileInputStream("server.properties"));
        int port =  Integer.parseInt(prop.getProperty("PORT"));
        try {
            System.out.println("\n==================== Detalles del servidor HTTP ====================\n");
            System.out.println("Server: WebServer_205");
            //System.out.println("Server Machine: "+ InetAddress.getLocalHost().getCanonicalHostName());
            System.out.println("Port number: " + Integer.parseInt(prop.getProperty("PORT")));
            System.out.println("Esperando peticiones...");
            System.out.println();
            serverSocket = new ServerSocket(port);
            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                socket = serverSocket.accept();
                // Create a ServerThread object, with the new connection as parameter
                ServerThread thread = new ServerThread(socket, prop);
                // Initiate thread using the start() method
                thread.start();
            }
        }catch(SocketTimeoutException e){
            System.err.println("Nothing received in 300 secs");
        } catch(Exception e){
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            if (socket != null){
                socket.close();
            }
        }
    }
}