package cz.jirimasek.dppstops;

import cz.jirimasek.dppstops.sources.Extractor;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 * <code>Extractor</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class Updater
{
    
    public void run()
    {
        try
        {
            Extractor stopDAO = new Extractor();
            
            stopDAO.retrieveStops();
        }
        catch (SAXException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
        catch (ParserConfigurationException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
        catch (XPathExpressionException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }        
        catch (MalformedURLException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
        catch (IOException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
        }
    }
}
