import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientTcpAdivina_Obj extends Thread {
    /* CLient TCP que ha endevinar un número pensat per SrvTcpAdivina_Obj.java */

    private String Nom;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Scanner scanner;
    private boolean continueConnected;
    private Tauler tauler;
    private Jugada jugador;
    private ServerMulticast serverMulticast;

    private ClientTcpAdivina_Obj(String hostname, int port, ServerMulticast serverMulticast) {
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (UnknownHostException ex) {
            System.out.println("Error de connexió. No existeix el host: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        continueConnected = true;
        scanner = new Scanner(System.in);
        jugador = new Jugada();
        this.serverMulticast = serverMulticast;
    }

    public void run() {
        String msg = null;
        while (continueConnected) {
            //Llegir info del servidor (estat del tauler)
            tauler = getRequest();

            //Crear codi de resposta a missatge
            switch (tauler.resultat) {
                case 3:
                    msg = "Benvingut al joc " + Nom + " - " + tauler.getNumPlayers();
                    break;
                case 2:
                    msg = "Més gran";
                    break;
                case 1:
                    msg = "Més petit";
                    break;
                case 0:
                    System.out.println("Correcte");
                    System.out.println(tauler);
                    continueConnected = false;
                    continue;
            }
            System.out.println(msg);
            System.out.println(tauler);

            if (tauler.resultat != 0) {
                System.out.println("Entra un número: ");
                jugador.num = scanner.nextInt();
                jugador.Nom = Nom;
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeObject(jugador);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        close(socket);
    }
    private Tauler getRequest() {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            tauler = (Tauler) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tauler;
    }


    private void close(Socket socket){
        //si falla el tancament no podem fer gaire cosa, només enregistrar
        //el problema
        try {
            //tancament de tots els recursos
            if(socket!=null && !socket.isClosed()){
                if(!socket.isInputShutdown()){
                    socket.shutdownInput();
                }
                if(!socket.isOutputShutdown()){
                    socket.shutdownOutput();
                }
                socket.close();
            }
        } catch (IOException ex) {
            //enregistrem l'error amb un objecte Logger
            Logger.getLogger(ClientTcpAdivina_Obj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        System.out.println("Posa el teu nom: ");
        Scanner jugador = new Scanner(System.in);
        String jugadorNou = jugador.nextLine();
        String serverIp = "localhost";
        int serverPort = 5558;

        try {
            // create ServerMulticast object
            Tauler tauler = new Tauler();
            ServerMulticast serverMulticast = new ServerMulticast(tauler, 5558);


            ClientTcpAdivina_Obj clientTcp = new ClientTcpAdivina_Obj(serverIp, serverPort, serverMulticast);
            clientTcp.Nom = jugadorNou;
            clientTcp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
