package cz.jirimasek.dppstops.sources;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import cz.jirimasek.dppstops.dao.LineDAO;
import cz.jirimasek.dppstops.dao.PlatformDAO;
import cz.jirimasek.dppstops.dao.RouteDAO;
import cz.jirimasek.dppstops.dao.StopDAO;
import cz.jirimasek.dppstops.dao.entities.GeoPoint;
import cz.jirimasek.dppstops.dao.entities.Line;
import cz.jirimasek.dppstops.dao.entities.Platform;
import cz.jirimasek.dppstops.dao.entities.Route;
import cz.jirimasek.dppstops.dao.entities.Stop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <code>Loader</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class Loader
{

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
     * @throws JSONException 
     */
    public void loadStops() throws JSONException
    {
        
        String json = loadResource("data/stops.json");
        
        JSONObject object = new JSONObject(json);
        
        JSONArray stops = object.getJSONArray("stops");
        
        for (int i = 0 ; i < stops.length() ; i++)
        {
            JSONObject stop = stops.getJSONObject(i);
            
            String name = stop.getString("name");
            JSONArray lines = stop.getJSONArray("lines");
            JSONArray platforms = stop.getJSONArray("platforms");
            
            // Get lines
            
            List<Key> lList = new LinkedList<Key>();
            
            for (int j = 0 ; j < lines.length() ; j++)
            {
                String number =  lines.getString(j);
                
                lList.add(getKey(List.class, number));
            }
            
            // Get platforms
            
            List<Key> pList = new LinkedList<Key>();
            
            for (int j = 0 ; j < platforms.length() ; j++)
            {
                JSONObject platform =  platforms.getJSONObject(j);
                
                Platform p = new Platform();
                
                p.setStop(getKey(Stop.class, name));
                
                if (platform.has("latitude"))
                {
                    Integer latitude = platform.getInt("latitude");
                    
                    p.setLatitude(latitude);
                }
                
                if (platform.has("longitude"))
                {
                    Integer longitude = platform.getInt("longitude");
                    
                    p.setLongitude(longitude);
                }
                
                if (platform.has("transport"))
                {
                    String transport = platform.getString("transport");
                    
                    p.setTransport(transport);
                }
                
                if (platform.has("lines"))
                {
                    JSONArray l = platform.getJSONArray("lines");
                    
                    List<Key> list = new ArrayList<Key>();
                    
                    for (int k = 0 ; k < l.length() ; k++)
                    {
                        String n = l.getString(k);
                    
                        list.add(getKey(Line.class, n));
                    }
                    
                    if (!list.isEmpty())
                    {
                        p.setLines(list);
                    }
                }
                
                getPlatformDAO().save(p);
                
                pList.add(p.getKey());
            }
            
            // Build stop
            
            Stop s = new Stop();
            
            s.setKey(getKey(Stop.class, name));
            s.setName(name);
            s.setLines(lList);
            s.setPlatforms(pList);
            
            getStopDAO().save(s);
        }
    }
    
    /**
     * 
     * @throws JSONException 
     */
    public void loadStops(String letter) throws JSONException
    {
        
        String json = loadResource(String.format("data/stops-%s.json", letter));
        
        JSONObject object = new JSONObject(json);
        
        JSONArray stops = object.getJSONArray("stops");
        
        for (int i = 0 ; i < stops.length() ; i++)
        {
            JSONObject stop = stops.getJSONObject(i);
            
            String name = stop.getString("name");
            JSONArray lines = stop.getJSONArray("lines");
            JSONArray platforms = stop.getJSONArray("platforms");
            
            // Get lines
            
            List<Key> lList = new LinkedList<Key>();
            
            for (int j = 0 ; j < lines.length() ; j++)
            {
                String number =  lines.getString(j);
                
                lList.add(getKey(List.class, number));
            }
            
            // Get platforms
            
            List<Key> pList = new LinkedList<Key>();
            
            for (int j = 0 ; j < platforms.length() ; j++)
            {
                JSONObject platform =  platforms.getJSONObject(j);
                
                Platform p = new Platform();
                
                p.setStop(getKey(Stop.class, name));
                
                if (platform.has("latitude"))
                {
                    Integer latitude = platform.getInt("latitude");
                    
                    p.setLatitude(latitude);
                }
                
                if (platform.has("longitude"))
                {
                    Integer longitude = platform.getInt("longitude");
                    
                    p.setLongitude(longitude);
                }
                
                if (platform.has("transport"))
                {
                    String transport = platform.getString("transport");
                    
                    p.setTransport(transport);
                }
                
                if (platform.has("lines"))
                {
                    JSONArray l = platform.getJSONArray("lines");
                    
                    List<Key> list = new ArrayList<Key>();
                    
                    for (int k = 0 ; k < l.length() ; k++)
                    {
                        String n = l.getString(k);
                    
                        list.add(getKey(Line.class, n));
                    }
                    
                    if (!list.isEmpty())
                    {
                        p.setLines(list);
                    }
                }
                
                getPlatformDAO().save(p);
                
                pList.add(p.getKey());
            }
            
            // Build stop
            
            Stop s = new Stop();
            
            s.setKey(getKey(Stop.class, name));
            s.setName(name);
            s.setLines(lList);
            s.setPlatforms(pList);
            
            getStopDAO().save(s);
        }
    }

    /**
     * 
     * @throws JSONException 
     */
    public void loadLines() throws JSONException
    {   
        String json = loadResource("data/lines.json");
        
        JSONObject object = new JSONObject(json);
        
        JSONArray lines = object.getJSONArray("lines");
        
        for (int i = 0 ; i < lines.length() ; i++)
        {
            JSONObject line = lines.getJSONObject(i);
            
            String number = line.getString("number");
            String transport = line.getString("transport");
            JSONArray routes = line.getJSONArray("routes");
            
            List<Key> list = new LinkedList<Key>();
            
            for (int j = 0 ; j < routes.length() ; j++)
            {
                JSONArray route = routes.getJSONArray(j);
                
                List<Key> l = new LinkedList<Key>();
                
                for (int k = 0 ; k <route.length() ; k++)
                {
                    String n = route.getString(k);
                    
                    l.add(getKey(Stop.class, n));
                }
                
                Route r = new Route();
                
                r.setStops(l);
                
                getRouteDAO().save(r);
                
                list.add(r.getKey());
            }
            
            Line l = new Line();
            
            l.setKey(getKey(Line.class, number));
            l.setNumber(number);
            l.setTransport(transport);
            l.setRoutes(list);
            
            getLineDAO().save(l);
        }
    }
    
    /**
     * 
     */
    public void deleteLines()
    {
        getLineDAO().delete();
        getRouteDAO().delete();
    }
    
    /**
     * 
     */
    public void deleteStops()
    {
        getStopDAO().delete();
        getPlatformDAO().delete();
    }
    
    /* ********************************************************************** */
    /* ************************** ACCESSORY METHODS ************************* */
    /* ********************************************************************** */
    /**
     * 
     * @param resource
     * @return 
     */
    private String loadResource(String resource)
    {
        InputStream is = this.getClass().getResourceAsStream(resource);

        try
        {
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            br.close();
            
            return sb.toString();
        }
        catch (UnsupportedEncodingException ex)
        {
            return null;
        }
        catch (IOException ex)
        {
            return null;
        }
    }

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
}
