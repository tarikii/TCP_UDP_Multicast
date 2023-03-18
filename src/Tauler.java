import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jordi on 05/03/17.
 */
public class Tauler implements Serializable {
    public Map<String,Integer> map_jugadors;
    public int resultat = 3, acabats;
    private int numPlayers;

    public Tauler() {
        map_jugadors = new HashMap<>();
        acabats = 0;
    }
    public int getNumPlayers() {
        return numPlayers;
    }

    public void addNUmPlayers() {
        this.numPlayers++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append("Intents\n");
        map_jugadors.forEach((k,v) -> sb.append(k + " - " + v + "\n"));
        return sb.toString();
    }
}

class Jugada implements Serializable {
    String Nom;
    int num;
}
