package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String args[]) throws IOException {

        int puerto = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
        if (args.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }

        try {
            // Create a server socket
            serverSocket = new ServerSocket(puerto);
            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                Socket server = serverSocket.accept();
                // Set the input channel
                BufferedReader input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                // Set the output channel
                PrintWriter output = new PrintWriter(server.getOutputStream(), true);
                // Receive the client message
                String message = input.readLine();
                System.out.println("SERVER: Received "+message+" my TCP server from: "+server.getLocalAddress()+":"+server.getPort());
                // Send response to the client
                output.println(message);
                System.out.println("SERVER: Sending: "+message+" my TCP server from: "+server.getLocalAddress()+":"+server.getPort());
                // Close the streams
                input.close();
                output.close();
            }

        } catch (SocketTimeoutException e) {
           System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (serverSocket != null){
                serverSocket.close();
            }

        }
    }
}
