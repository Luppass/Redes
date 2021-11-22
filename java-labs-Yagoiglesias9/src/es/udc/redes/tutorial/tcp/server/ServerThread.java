package es.udc.redes.tutorial.tcp.server;
import java.net.*;
import java.io.*;

/** Thread that processes an echo server connection. */

public class ServerThread extends Thread {

  private Socket socket;

  public ServerThread(Socket s) {
    this.socket = s;
  }

  public void run() {
    try {
      // Set the input channel
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // Set the output channel
      PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
      // Receive the message from the client
      String message = input.readLine();
      System.out.println("SERVER: Received " + message + " my TCP server from: " + socket.getLocalAddress() + ":" + socket.getPort());
      // Sent the echo message to the client
      output.println(message);
      System.out.println("SERVER: Sending: " + message + " my TCP server from: " + socket.getLocalAddress() + ":" + socket.getPort());
      // Close the streams
      input.close();
      output.close();

    } catch (SocketTimeoutException e) {
      System.err.println("Nothing received in 300 secs");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      } finally {
      try {
        if (socket != null){
          socket.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}


