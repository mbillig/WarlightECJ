package warlight2;

import ec.gp.GPData;

/**
 * Created by Jonatan on 15-Sep-15.
 */
public class DoubleData extends GPData{
    public double x;

    public void copyTo(final GPData gpd){
        ((DoubleData)gpd).x = x;
    }
}
