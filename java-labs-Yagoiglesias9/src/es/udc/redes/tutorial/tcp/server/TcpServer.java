package es.udc.redes.tutorial.tcp.server;
import java.io.IOException;
import java.net.*;

/** Multithread TCP echo server. */

public class TcpServer {



  public static void main(String argv[]) throws IOException {
    Socket socket = null;
    int puerto = Integer.parseInt(argv[0]);
    ServerSocket serverSocket = null;
    if (argv.length != 1) {
      System.err.println("Format: es.udc.redes.tutorial.tcp.server.TcpServer <port>");
      System.exit(-1);
    }
    try {
      serverSocket = new ServerSocket(puerto);
      // Set a timeout of 300 secs
      serverSocket.setSoTimeout(300000);
      while (true) {
        // Wait for connections
        socket = serverSocket.accept();
        // Create a ServerThread object, with the new connection as parameter
        ServerThread thread = new ServerThread(socket);
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
