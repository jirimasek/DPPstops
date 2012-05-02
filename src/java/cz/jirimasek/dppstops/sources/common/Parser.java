package cz.jirimasek.dppstops.sources.common;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Parser
{

    public Document parse(String html) throws SAXException, IOException,
            ParserConfigurationException
    {
        html = html.replaceAll("<br>", "<br/>");
        
        // Step 1: create a DocumentBuilderFactory
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(html));

        // Step 2: create a DocumentBuilder

        DocumentBuilder db = dbf.newDocumentBuilder();

        db.setEntityResolver(new EntityResolver()
        {
            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException
            {
                if (systemId.contains("xhtml1-strict.dtd"))
                {
                    return new InputSource(new StringReader(""));
                }
                else
                {
                    return null;
                }
            }
        });

        // Step 3: parse the input file to get a Document object
        try
        {
            return db.parse(is);
        }
        catch (SAXException ex)
        {
            System.err.println(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
            System.err.println(ex.getCause());
            
            throw ex;
        }
    }

}
