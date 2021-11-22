package es.udc.redes.tutorial.udp.server;

import java.net.*;


/**
 * Implements a UDP echo sqerver.
 */
public class UdpServer {

    public static void main(String argv[]) {
        int puerto = Integer.parseInt(argv[0]);

        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }

        DatagramSocket datagramSocket = null;


        try {
            // Create a server socket
            datagramSocket = new DatagramSocket(puerto);
            // Set max. timeout to 300 secs
            datagramSocket.setSoTimeout(300000);
            while (true) {
                // Prepare datagram for reception
                byte[] Buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(Buffer, Buffer.length);
                // Receive the message
                datagramSocket.receive(packet);
                System.out.println("SERVER: Received "
                        + new String(packet.getData(), 0, packet.getLength())
                        + " from " + packet.getAddress().toString() + ":"
                        + packet.getPort());
                // Prepare datagram to send response
                String recibido = new String(packet.getData());
                DatagramPacket response = new DatagramPacket(recibido.getBytes(),
                                                    recibido.getBytes().length, packet.getAddress(), packet.getPort());
                // Send response
                datagramSocket.send(response);
                System.out.println("SERVER: Sending "
                        + new String(response.getData()) + " to "
                        + response.getAddress().toString() + ":"
                        + response.getPort());

            }
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (datagramSocket != null){
                datagramSocket.close();
            }

        }
    }
}
