import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ServerMulticast implements Runnable {
    Tauler tauler;
    private InetAddress group;
    private MulticastSocket socket;
    int port;

    public ServerMulticast(Tauler tauler, int port) throws IOException {
        group = InetAddress.getByName("230.0.0.0");
        socket = new MulticastSocket();
        this.tauler = tauler;
        this.port = port;
    }

    public void run() {
        try {
            while (tauler.acabats < tauler.getNumPlayers()) {
                enviar(tauler);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Servidor iniciat amb éxit!.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviar(Tauler tauler) throws IOException {
        byte[] enviarDades = tauler.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(enviarDades, enviarDades.length, group, 5555);
        socket.send(packet);
    }
}
