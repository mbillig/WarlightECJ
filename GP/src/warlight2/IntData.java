package warlight2;

import ec.gp.GPData;

/**
 * Created by Jonatan on 15-Sep-15.
 */
public class IntData extends GPData{
    public int x;

    public void copyTo(final GPData gpd){
        ((IntData)gpd).x = x;
    }
}
