package cz.jirimasek.dppstops.common;

/**
 * <code>NumericUtils</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class NumericUtils
{

    public static boolean isInteger(String string)
    {
        try
        {
            Integer.parseInt(string);
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        
        return true;
    }
}
