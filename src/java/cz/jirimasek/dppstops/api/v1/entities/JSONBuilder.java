package cz.jirimasek.dppstops.api.v1.entities;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import cz.jirimasek.dppstops.common.GPSUtils;
import cz.jirimasek.dppstops.dao.LineDAO;
import cz.jirimasek.dppstops.dao.PlatformDAO;
import cz.jirimasek.dppstops.dao.RouteDAO;
import cz.jirimasek.dppstops.dao.StopDAO;
import cz.jirimasek.dppstops.dao.entities.GeoPoint;
import cz.jirimasek.dppstops.dao.entities.Line;
import cz.jirimasek.dppstops.dao.entities.Platform;
import cz.jirimasek.dppstops.dao.entities.Route;
import cz.jirimasek.dppstops.dao.entities.Stop;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <code>JSONBuilder</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class JSONBuilder
{

    private StopDAO stopDAO;
    private LineDAO lineDAO;
    private PlatformDAO platformDAO;
    private RouteDAO routeDAO;
    private Map<Key, Stop> stops;

    /* ********************************************************************** */
    /* ************************ GETTERS AND SETTERS ************************* */
    /* ********************************************************************** */
    /**
     * 
     * @return 
     */
    private LineDAO getLineDAO()
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
    private PlatformDAO getPlatformDAO()
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
    private RouteDAO getRouteDAO()
    {
        if (routeDAO == null)
        {
            routeDAO = new RouteDAO();
        }

        return routeDAO;
    }

    /**
     * 
     * @return 
     */
    private StopDAO getStopDAO()
    {
        if (stopDAO == null)
        {
            stopDAO = new StopDAO();
        }

        return stopDAO;
    }

    /* ********************************************************************** */
    /* ************************** EXECUTION METHODS ************************* */
    /* ********************************************************************** */
    /**
     * 
     * @return
     * @throws JSONException 
     */
    public JSONObject getStops() throws JSONException
    {
        List<Stop> stops = getStopDAO().get();

        JSONArray array = new JSONArray();

        for (Stop stop : stops)
        {
            JSONArray lines = new JSONArray();

            for (Key key : stop.getLines())
            {
                lines.put(key.getName());
            }

            JSONArray platforms = new JSONArray();

            for (Key key : stop.getPlatforms())
            {
                Platform platform = getPlatformDAO().get(key);

                JSONArray arr = new JSONArray();

                for (Key k : platform.getLines())
                {
                    arr.put(k.getName());
                }

                JSONObject pl = new JSONObject();

                pl.put("latitude", platform.getLatitude());
                pl.put("longitude", platform.getLongitude());

                pl.put("transport", platform.getTransport());
                pl.put("lines", arr);

                platforms.put(pl);
            }

            JSONObject obj = new JSONObject();

            obj.put("name", stop.getName());
            obj.put("lines", lines);
            obj.put("platforms", platforms);

            array.put(obj);
        }

        JSONObject output = new JSONObject();

        output.put("stops", array);

        return output;
    }

    /**
     * 
     * @param lat
     * @param lng
     * @param dist
     * @return
     * @throws JSONException 
     */
    public JSONObject getPlatforms(double lat, double lng, int dist) throws JSONException
    {
        GeoPoint point = new GeoPoint();

        point.setLatitude((int) (lat * 1E6));
        point.setLongitude((int) (lng * 1E6));

        GeoPoint[] boundaries = point.boundingCoordinates(dist * 1E-3);

        List<Platform> platforms = getPlatformDAO().getInSquare(boundaries[0],
                boundaries[1]);

        // Order platforms by distance

        Map<Integer, Platform> sortedPlatforms = new TreeMap<Integer, Platform>();
        Map<Key, Platform> unsortedPlatforms = new HashMap<Key, Platform>();
        Map<Key, Integer> distances = new HashMap<Key, Integer>();

        for (Platform platform : platforms)
        {
            double lat2 = platform.getLatitude() * 1E-6;
            double lng2 = platform.getLongitude() * 1E-6;

            double miles = GPSUtils.distFrom(lat, lng, lat2, lng2);

            int meters = GPSUtils.miles2meters(miles);

            sortedPlatforms.put(new Integer(meters), platform);
            unsortedPlatforms.put(platform.getKey(), platform);
            distances.put(platform.getKey(), new Integer(meters));
        }
        System.out.println(distances.size());
        // Filter platforms according to proper distance

        int i = 0;

        Map<Key, Stop> nearestStops = new HashMap<Key, Stop>();

        for (Integer distance : sortedPlatforms.keySet())
        {
            if (distance.intValue() > dist)
            {
                break;
            }

            Platform platform = sortedPlatforms.get(distance);

            Key k = platform.getStop();

            if (!nearestStops.containsKey(k))
            {
                nearestStops.put(k, getStop(k));
            }
        }

        // Build output object

        JSONArray array = new JSONArray();

        for (Key key : nearestStops.keySet())
        {
            Stop stop = nearestStops.get(key);

            JSONArray lines = new JSONArray();

            for (Key k : stop.getLines())
            {
                lines.put(k.getName());
            }

            JSONArray p = new JSONArray();

            for (Key k : stop.getPlatforms())
            {
                Platform platform;
                
                if (unsortedPlatforms.containsKey(k))
                {
                    platform = unsortedPlatforms.get(k);
                }
                else
                {
                    platform = getPlatformDAO().get(k);
                }    
                
                JSONArray arr = new JSONArray();

                for (Key kk : platform.getLines())
                {
                    arr.put(kk.getName());
                }

                JSONObject pl = new JSONObject();

                pl.put("latitude", platform.getLatitude());
                pl.put("longitude", platform.getLongitude());
                
                if (distances.containsKey(k))
                {
                    pl.put("distance", distances.get(k).intValue());
                }
                else
                {
                    double lat2 = platform.getLatitude() * 1E-6;
                    double lng2 = platform.getLongitude() * 1E-6;

                    double miles = GPSUtils.distFrom(lat, lng, lat2, lng2);

                    int meters = GPSUtils.miles2meters(miles);
            
                    pl.put("distance", meters);
                }

                pl.put("transport", platform.getTransport());
                pl.put("lines", arr);

                p.put(pl);
            }

            JSONObject obj = new JSONObject();

            obj.put("name", stop.getName());
            obj.put("lines", lines);
            obj.put("platforms", p);

            array.put(obj);
        }

        JSONObject output = new JSONObject();

        output.put("stops", array);

        return output;
    }

    /**
     * 
     * @return
     * @throws JSONException 
     */
    public JSONObject getLines() throws JSONException
    {
        List<Line> lines = getLineDAO().get();

        JSONObject output = new JSONObject();

        JSONArray array = new JSONArray();

        for (Line line : lines)
        {
            JSONArray arr = new JSONArray();

            for (Key key : line.getRoutes())
            {
                Route route = getRouteDAO().get(key);

                JSONArray r = new JSONArray();

                for (Key k : route.getStops())
                {
                    r.put(k.getName());
                }

                arr.put(r);
            }

            JSONObject obj = new JSONObject();

            obj.put("number", line.getNumber());
            obj.put("transport", line.getTransport());
            obj.put("routes", arr);

            array.put(obj);
        }

        output.put("lines", array);

        return output;
    }

    /**
     * 
     * @param number
     * @return 
     */
    public JSONObject getLine(String number) throws JSONException
    {
        Key key = getKey(Line.class, number);

        Line line = getLineDAO().get(key);

        if (line == null)
        {
            return null;
        }

        JSONArray routes = new JSONArray();

        for (Key k : line.getRoutes())
        {
            Route r = getRouteDAO().get(k);

            JSONArray route = new JSONArray();

            for (Key kk : r.getStops())
            {
                Stop s = getStop(kk);

                JSONArray platforms = new JSONArray();

                if (s != null)
                {

                    for (Key kkk : s.getPlatforms())
                    {
                        Platform p = getPlatformDAO().get(kkk);

                        if (p.getLines().contains(key))
                        {
                            JSONObject platform = new JSONObject();

                            platform.put("latitude", p.getLatitude());
                            platform.put("longitude", p.getLongitude());

                            platforms.put(platform);
                        }
                    }
                }

                JSONObject stop = new JSONObject();

                stop.put("name", kk.getName());
                stop.put("platforms", platforms);

                route.put(stop);
            }

            routes.put(route);
        }

        JSONObject output = new JSONObject();

        output.put("number", line.getNumber());
        output.put("tranport", line.getTransport());
        output.put("routes", routes);

        return output;
    }

    /* ********************************************************************** */
    /* ************************** ACCESSORY METHODS ************************* */
    /* ********************************************************************** */
    /**
     * 
     * @param key
     * @return 
     */
    private Stop getStop(Key key)
    {
        if (stops == null)
        {
            stops = new HashMap<Key, Stop>();
        }

        if (!stops.containsKey(key))
        {
            Stop stop = getStopDAO().get(key);

            stops.put(key, stop);
        }

        return stops.get(key);
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
