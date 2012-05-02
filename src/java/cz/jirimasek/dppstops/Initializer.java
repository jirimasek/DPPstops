package cz.jirimasek.dppstops;

import cz.jirimasek.dppstops.sources.Loader;
import org.codehaus.jettison.json.JSONException;

/**
 * <code>Initializer</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class Initializer
{

    /**
     * 
     * @param letter 
     */
    public void initializeStops()
    {
        try
        {
            Loader loader = new Loader();

            loader.loadStops();
        }
        catch (JSONException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }

    /**
     * 
     * @param letter 
     */
    public void initializeStops(String letter)
    {
        try
        {
            Loader loader = new Loader();

            loader.loadStops(letter);
        }
        catch (JSONException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }

    /**
     * 
     */
    public void initializeLines()
    {
        try
        {
            Loader loader = new Loader();

            loader.loadLines();
        }
        catch (JSONException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }
    
    /**
     * 
     */
    public void deleteStops()
    {
        Loader loader = new Loader();
        
        loader.deleteStops();
    }
    
    /**
     * 
     */
    public void deleteLines()
    {
        Loader loader = new Loader();
        
        loader.deleteLines();
    }
}
