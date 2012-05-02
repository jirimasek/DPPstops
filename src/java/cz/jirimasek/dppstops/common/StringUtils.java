package cz.jirimasek.dppstops.common;

/**
 * <code>StringUtils</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class StringUtils
{
    
    /**
     * 
     * @param string
     * @return 
     */
    public static boolean isNullOrEmpty(String string)
    {
        
        if (string == null || string.isEmpty())
        {
            return true;
        }
        
        return false;
    }
}
