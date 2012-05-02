package cz.jirimasek.dppstops.sources;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import cz.jirimasek.dppstops.common.NumericUtils;
import cz.jirimasek.dppstops.dao.LineDAO;
import cz.jirimasek.dppstops.dao.PlatformDAO;
import cz.jirimasek.dppstops.dao.RouteDAO;
import cz.jirimasek.dppstops.dao.StopDAO;
import cz.jirimasek.dppstops.dao.entities.GeoPoint;
import cz.jirimasek.dppstops.dao.entities.Line;
import cz.jirimasek.dppstops.dao.entities.Platform;
import cz.jirimasek.dppstops.dao.entities.Route;
import cz.jirimasek.dppstops.dao.entities.Stop;
import cz.jirimasek.dppstops.sources.common.Loader;
import cz.jirimasek.dppstops.sources.common.Parser;
import cz.jirimasek.dppstops.sources.common.XPathProcessor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <code>DataSource</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class Extractor
{

    private static final String BUS = "Bus";
    private static final String TRAM = "Tram";
    private static final String SUBWAY = "Subway";
    private static final String SUBWAY_ENTRY = "Subway entry";
    private static final String FUNICULAR = "Funicular";
    private static final String FERRY = "Ferry";
    private final String BASE_URI = "http://jrportal.dpp.cz/jrportal/";
    private final String STOPS_INIT_URI = "StopList.aspx?t=0&mi=14&n=27";
    private final String LINES_INIT_URI = "LineList.aspx?mi=3&t=1";
    private final String MAPS_QUERY_URI = "http://mapy.cz/search?query=";
    private final String MAPS_DESC_URI = "http://mapy.cz/basepoi/description?id=";
    private Loader loader;
    private Parser parser;
    private XPathProcessor xpp;
    private StopDAO stopDAO;
    private LineDAO lineDAO;
    private PlatformDAO platformDAO;
    private RouteDAO routeDAO;

    /* ********************************************************************** */
    /* ************************ GETTERS AND SETTERS ************************* */
    /* ********************************************************************** */
    /**
     * 
     * @return 
     */
    private Loader getLoader()
    {
        if (loader == null)
        {
            loader = new Loader();
        }

        return loader;
    }

    /**
     * 
     * @return 
     */
    private Parser getParser()
    {
        //if (parser == null)
        {
            parser = new Parser();
        }

        return parser;
    }

    /**
     * 
     * @return 
     */
    private XPathProcessor getXPP()
    {
        if (xpp == null)
        {
            xpp = new XPathProcessor();
        }

        return xpp;
    }

    /**
     * 
     * @return 
     */
    public StopDAO getStopDAO()
    {
        if (stopDAO == null)
        {
            stopDAO = new StopDAO();
        }

        return stopDAO;
    }

    /**
     * 
     * @return 
     */
    public LineDAO getLineDAO()
    {
        if (lineDAO == null)
        {
            lineDAO = new LineDAO();
        }

        return lineDAO;
    }

    /**
     * 
     * @return 
     */
    public PlatformDAO getPlatformDAO()
    {
        if (platformDAO == null)
        {
            platformDAO = new PlatformDAO();
        }

        return platformDAO;
    }

    /**
     * 
     * @return 
     */
    public RouteDAO getRouteDAO()
    {
        if (routeDAO == null)
        {
            routeDAO = new RouteDAO();
        }

        return routeDAO;
    }

    /* ********************************************************************** */
    /* ************************** EXECUTION METHODS ************************* */
    /* ********************************************************************** */
    /**
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    public void retrieveStops() throws MalformedURLException, IOException,
            XPathExpressionException, SAXException, ParserConfigurationException
    {
        getStopDAO().delete();
        getLineDAO().delete();
        getPlatformDAO().delete();
        getRouteDAO().delete();

        /*List<URL> stopListsURL = getStopListsURL();

        for (URL url : stopListsURL)
        {
            extractStops(url);
        }*/
        
        List<URL> linesListsURL = getLinesListsURL();
        
        for (URL url : linesListsURL)
        {
            extractLineRoutes(url);
        }
    }

    /* ********************************************************************** */
    /* ******************************* STOPS ******************************** */
    /* ********************************************************************** */
    /**
     * 
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    private List<URL> getStopListsURL() throws XPathExpressionException,
            SAXException, IOException, ParserConfigurationException,
            MalformedURLException
    {
        String html = getLoader().load(new URL(BASE_URI + STOPS_INIT_URI));
        Document document = getParser().parse(html);
        NodeList alphabet = getXPP().evaluate(document,
                "//ul[@class=\"abeceda\"]/li/a");

        List<URL> list = new LinkedList<URL>();

        for (int i = 0 ; i < alphabet.getLength() ; i++)
        {
            Node node = alphabet.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Node href = attrs.getNamedItem("href");
            URL url = new URL(BASE_URI + href.getNodeValue());

            list.add(url);
        }

        return list;
    }

    /**
     * 
     * @param url
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws SAXException 
     */
    private void extractStops(URL url) throws ParserConfigurationException,
            XPathExpressionException, IOException, SAXException
    {
        // Retrieve page wish stop list

        String html = getLoader().load(url);
        Document document = getParser().parse(html);
        NodeList nodes = getXPP().evaluate(document,
                "//table[@class=\"zastavky\"]/tr");

        // Process stops on the list

        for (int i = 0 ; i < nodes.getLength() ; i++)
        {
            Node node = nodes.item(i);

            String name = node.getFirstChild().getTextContent();
            NodeList nodelist = node.getLastChild().getChildNodes();

            // Get lines which served this stop

            List<Key> stopLines = new LinkedList<Key>();

            for (int j = 0 ; j < nodelist.getLength() ; j++)
            {
                Node line = nodelist.item(j);

                if (line.getNodeName().equals("a"))
                {
                    Key key = getLine(line.getTextContent());

                    stopLines.add(key);
                }
            }

            // Get platforms of this stop

            List<Key> platforms = extractPlatforms(name);

            // Save stop

            Stop stop = new Stop();

            stop.setKey(getKey(Stop.class, name));
            stop.setName(name.replaceAll("\\s+-\\s+[ABC]$" , ""));
            stop.setLines(stopLines);
            stop.setPlatforms(platforms);

            getStopDAO().save(stop);
        }
    }

    /**
     * 
     * @param name
     * @return 
     */
    private String getLineType(String name)
    {
        try
        {
            if (NumericUtils.isInteger(name))
            {
                int number = Integer.parseInt(name);

                if (number < 100)
                {
                    return TRAM;
                }
                else
                {
                    return BUS;
                }
            }
            else
            {
                if (name.equals("A") || name.equals("B") || name.equals("C"))
                {
                    return SUBWAY;
                }
                else if (name.startsWith("X"))
                {
                    int number = Integer.parseInt(name.substring(1));

                    if (number < 100)
                    {
                        return TRAM;
                    }
                    else
                    {
                        return BUS;
                    }
                }
                else if (name.startsWith("P"))
                {
                    if (NumericUtils.isInteger(name.substring(1)))
                    {
                        return FERRY;
                    }
                }
                else if (name.startsWith("H"))
                {
                    if (NumericUtils.isInteger(name.substring(1)))
                    {
                        return BUS;
                    }
                }
                else if (name.equals("LD"))
                {
                    return FUNICULAR;
                }
            }
        }
        catch (NumberFormatException ex)
        {
            return null;
        }

        return null;
    }

    /**
     * 
     * @param name
     * @return
     * @throws SAXException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws ParserConfigurationException 
     */
    private List<Key> extractPlatforms(String name) throws SAXException,
            UnsupportedEncodingException, IOException, ParserConfigurationException
    {
        // Load search result for the stop from Mapy.cz

        String decodedquery = String.format("MHD %s Praha", name);
        String encodedquery = URLEncoder.encode(decodedquery, "UTF-8");

        URL searchURI = new URL(MAPS_QUERY_URI + encodedquery);

        String searchXML = getLoader().load(searchURI);
        Document search = getParser().parse(searchXML);

        // Process retrieved results

        Map<Integer, GeoPoint> geoPoints = new HashMap<Integer, GeoPoint>();

        NodeList results = search.getFirstChild().getChildNodes();

        for (int i = 0 ; i < results.getLength() ; i++)
        {
            Node node = results.item(i);

            if (node.getNodeName().equals("poi"))
            {
                NamedNodeMap d = node.getAttributes();

                String id = d.getNamedItem("id").getTextContent();
                String latitude = d.getNamedItem("y").getTextContent();
                String longitude = d.getNamedItem("x").getTextContent();

                int lat = (int) (Float.parseFloat(latitude) * 1E6);
                int lon = (int) (Float.parseFloat(longitude) * 1E6);

                GeoPoint gp = new GeoPoint();

                gp.setLatitude(new Integer(lat));
                gp.setLongitude(new Integer(lon));

                geoPoints.put(new Integer(id), gp);
            }
        }

        // Load more info for retrieved results

        StringBuilder ids = new StringBuilder();

        for (Integer id : geoPoints.keySet())
        {
            if (ids.length() > 0)
            {
                ids.append("%2C");
            }

            ids.append(id.toString());
        }

        URL descURI = new URL(MAPS_DESC_URI + ids.toString());

        String descXML = getLoader().load(descURI);
        Document desc = getParser().parse(descXML);

        // Process retrieved info

        List<Key> platforms = new LinkedList<Key>();

        NodeList descriptions = desc.getFirstChild().getChildNodes();

        for (int i = 0 ; i < descriptions.getLength() ; i++)
        {
            Node node = descriptions.item(i);

            if (node.getNodeName().equals("poi"))
            {
                NamedNodeMap d = node.getAttributes();

                String id = d.getNamedItem("id").getTextContent();

                String title = null;
                String description = null;

                NodeList sub = node.getChildNodes();

                for (int j = 0 ; j < sub.getLength() ; j++)
                {
                    Node n = sub.item(j);

                    if (n.getNodeName().equals("title"))
                    {
                        title = n.getTextContent();
                    }
                    else if (n.getNodeName().equals("description"))
                    {
                        description = n.getTextContent();

                        break;
                    }
                }

                String type = getPlatformType(title, name);

                if (type != null)
                {
                    Platform platform = new Platform();

                    platform.setStop(getKey(Stop.class, name));
                    platform.setTransport(type);
                    platform.setLatitude(geoPoints.get(new Integer(id)).getLatitude());
                    platform.setLongitude(geoPoints.get(new Integer(id)).getLongitude());
                    platform.setLines(getPlatformLines(title, description, type));

                    getPlatformDAO().save(platform);

                    platforms.add(platform.getKey());
                }
            }
        }

        return platforms;
    }

    /**
     * 
     * @param title
     * @param name
     * @return 
     */
    private String getPlatformType(String title, String name)
    {
        String exp1 = String.format("autobusová zastávka %s", name);
        String exp2 = String.format("tramvajová zastávka %s", name);
        String exp3 = String.format("vstup do stanice metra %s", name);
        String exp4 = String.format("%s, Metro", name);

        if (title.equals(exp1))
        {
            return BUS;
        }
        else if (title.equals(exp2))
        {
            return TRAM;
        }
        else if (title.equals(exp3))
        {
            return SUBWAY_ENTRY;
        }
        else if (title.equals(exp4))
        {
            return SUBWAY;
        }

        return null;
    }

    /**
     * 
     * @param title
     * @param descrition
     * @param type
     * @return 
     */
    private List<Key> getPlatformLines(String title, String descrition,
            String type)
    {
        List<Key> list = new LinkedList<Key>();

        if (type.equals(SUBWAY))
        {
            Key key = getLine(title.substring(title.length() - 1));

            list.add(key);
        }
        else if (type.equals(SUBWAY_ENTRY))
        {
            Key key = getLine(descrition.substring(descrition.length() - 1));

            list.add(key);
        }
        else
        {
            String[] s = descrition.split("\\s+");

            if (s.length == 2)
            {
                String[] a = s[1].split(",");

                for (int i = 0 ; i < a.length ; i++)
                {
                    Key key = getLine(a[i]);

                    list.add(key);
                }
            }
        }

        return list;
    }

    /* ********************************************************************** */
    /* ******************************* LINES ******************************** */
    /* ********************************************************************** */
    /**
     * 
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException 
     */
    private List<URL> getLinesListsURL() throws IOException, SAXException,
            MalformedURLException, XPathExpressionException, ParserConfigurationException
    {
        
        // Get init page with menu
        
        String html = getLoader().load(new URL(BASE_URI + LINES_INIT_URI));
        //jr-menu-top
        Document document = getParser().parse(html);
        NodeList menu = getXPP().evaluate(document,
                "//li[@class=\"open\"]/ul/li/a");
        
        List<URL> lists = new LinkedList<URL>();

        for (int i = 0 ; i < menu.getLength() ; i++)
        {
            Node node = menu.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Node href = attrs.getNamedItem("href");
            String str = href.getNodeValue();

            if (str.startsWith("LineList.aspx"))
            {
                URL url = new URL(BASE_URI + str);

                lists.add(url);
                System.out.println("Velka: " + url.toString());
                String h = getLoader().load(url);
        
                Document d = getParser().parse(h);
                NodeList m = getXPP().evaluate(d,
                    "//div[@id=\"jr-menu-top\"]/ul/li/a");
                System.out.println(m.getLength());
                if (m.getLength() > 1)
                {
                    for (int j = 1 ; j < m.getLength() ; j++)
                    {
                        Node n = m.item(j);
                        NamedNodeMap a = n.getAttributes();
                        Node hr = a.getNamedItem("href");
                        String s = hr.getNodeValue();
                        
                        if (s.startsWith("LineList.aspx"))
                        {
                            URL u = new URL(BASE_URI + s);
                            
                            lists.add(u);
                        }
                    }
                }
            }
        }
        
        // Get every page with link to line page
 
        List<URL> list = new LinkedList<URL>();

        for (URL url : lists)
        {
            String src = getLoader().load(url);
            Document doc = getParser().parse(src);
            NodeList boxes = getXPP().evaluate(doc,
                    "//div[@id=\"pole\"]/div/p/a");

            for (int i = 0 ; i < boxes.getLength() ; i++)
            {
                Node node = boxes.item(i);
                NamedNodeMap attrs = node.getAttributes();
                Node href = attrs.getNamedItem("href");
                String str = href.getNodeValue();

                URL u = new URL(BASE_URI + str);

                list.add(u);
            }
        }

        return list;
    }

    /**
     * 
     * @param url
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException 
     */
    public void extractLineRoutes(URL url) throws IOException, SAXException,
            XPathExpressionException, ParserConfigurationException
    {
        // Get init page with menu
        
        String html = getLoader().load(url);
        Document document = getParser().parse(html);
        NodeList h1 = getXPP().evaluate(document, "//h1");
        NodeList tables = getXPP().evaluate(document,
                    "//div[@id=\"pole\"]/table");
        
            if (h1.getLength() > 0)
            {
                String[] number = h1.item(0).getTextContent().split("\\s+");
                
                Key key = getKey(Line.class, number[1]);
                Line line = getLineDAO().get(key);
                
                if (line == null)
                {
                    line = new Line();
                    
                    line.setKey(key);
                    line.setNumber(number[1]);
                    line.setTransport(getLineType(number[1]));
                }
                
                List<Key> routes = new LinkedList<Key>();
                
                for (int i = 0 ; i < tables.getLength() ; i++)
                {
                    Node node = tables.item(i);
                    NodeList nodelist = node.getChildNodes();
                    
                    List<Key> stops = new LinkedList<Key>();
                    
                    String destination = null;
                    
                    for (int j = 0 ; j < nodelist.getLength() ; j++)
                    {
                        Node n = nodelist.item(j);
                        
                        if (n.getNodeName().equals("tr"))
                        {   
                            String name = n.getTextContent().replaceAll("\\s+-\\s+[ABC]$" , "");
        
                            Key k = getKey(Stop.class, name);
                            
                            stops.add(k);
                        }
                        else if (n.getNodeName().equals("caption"))
                        {
                            destination = n.getTextContent().substring(5);
                        }
                    }
                    
                    Key k = getKey(Stop.class, destination);
                            
                    stops.add(k);
                    
                    Route route = new Route();
                    
                    route.setStops(stops);
                    
                    getRouteDAO().save(route);
                    
                    routes.add(route.getKey());
                }
                
                line.setRoutes(routes);
                
                getLineDAO().save(line);
            }
    }
    /* ********************************************************************** */
    /* ************************** ACCESSORY METHODS ************************* */
    /* ********************************************************************** */

    /**
     * 
     * @param clazz
     * @param value
     * @return 
     */
    private Key getKey(Class clazz, String value)
    {
        Key key = KeyFactory.createKey(clazz.getSimpleName(), value);

        return key;
    }

    /**
     * 
     * @param number
     * @return 
     */
    private Key getLine(String number)
    {
        Key key = getKey(Line.class, number);
        Line line = getLineDAO().get(key);

        if (line == null)
        {
            line = new Line();
            line.setKey(key);
            line.setNumber(number);
            line.setTransport(getLineType(number));

            getLineDAO().save(line);
        }

        return line.getKey();
    }
}
