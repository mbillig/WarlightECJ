package texasholdem;

import ec.Evolve;

/**
 * Created by Jonatan on 06-Oct-15.
 */
public class TexasRunner {
    public static void main(String[] args) {
        String[] texas = new String[]{"-file", "GP\\src\\texasholdem\\texas.params", "-p", "gp.tree.print-style=latex"};
        Evolve.main(texas);
    }
}
