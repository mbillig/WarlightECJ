import com.theaigames.game.warlight2.Warlight2;
import ec.Evolve;

/**
 * Created by Jonatan on 21-Sep-15.
 */
public class WarlightRunner {
    public static void main(String[] args) throws Exception {
        String [] senseWorld = new String[]{"-file", "GP\\src\\sense_world.params"};

        Evolve.main(senseWorld);
    }
}
