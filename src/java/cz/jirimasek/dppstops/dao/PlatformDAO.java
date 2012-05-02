package cz.jirimasek.dppstops.dao;

import com.google.appengine.api.datastore.Key;
import cz.jirimasek.dppstops.dao.entities.GeoPoint;
import cz.jirimasek.dppstops.dao.entities.Platform;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * <code>PlatformDAO</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class PlatformDAO
{

    private PersistenceManager persistenceManager;

    /**
     * Vytvoří novou instanci třídy <code>PlatformDAO</code>.
     */
    public PlatformDAO()
    {
        persistenceManager = PMF.get().getPersistenceManager();
    }

    /**
     * 
     * @param key
     * @return 
     */
    public Platform get(Key key)
    {
        try
        {
            Platform platform = persistenceManager.getObjectById(Platform.class, key);
            
            return persistenceManager.detachCopy(platform);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 
     * @return 
     */
    public List<Platform> get()
    {
        String sql = "select from " + Platform.class.getName();
        Query query = persistenceManager.newQuery(sql);
        
        return (List<Platform>) query.execute();
    }

    public List<Platform> getInSquare(GeoPoint geoPoint, GeoPoint geoPoint0)
    {
        Query query = persistenceManager.newQuery(Platform.class);
        
        query.setFilter("latitude > lat1 && latitude < lat2");
        query.declareParameters("Integer lat1, Integer lat2");
        
        Integer lat1 = geoPoint.getLatitude();
        Integer lng1 = geoPoint.getLongitude();
        Integer lat2 = geoPoint0.getLatitude();
        Integer lng2 = geoPoint0.getLongitude();
        
        List<Platform> platforms = (List<Platform>) query.execute(lat1, lat2);
        
        List<Platform> list = new LinkedList<Platform>();
        
        for (Platform platform : platforms)
        {
            if (platform.getLongitude() > lng1 && platform.getLongitude() < lng2)
            {
                list.add(platform);
            }
        }
        
        return list;
    }

    /**
     * 
     * @param platform 
     */
    public void save(Platform platform)
    {
        try
        {
            persistenceManager.makePersistent(platform);
        }
        finally
        {
            persistenceManager.flush();
        }
    }

    /**
     * 
     */
    public void delete()
    {   
        persistenceManager.deletePersistentAll(get());
        
        persistenceManager.flush();
    }
}
