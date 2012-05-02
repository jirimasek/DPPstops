package cz.jirimasek.dppstops.dao;

import com.google.appengine.api.datastore.Key;
import cz.jirimasek.dppstops.dao.entities.Route;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * <code>RouteDAO</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class RouteDAO
{

    private PersistenceManager persistenceManager;

    /**
     * Vytvoří novou instanci třídy <code>RouteDAO</code>.
     */
    public RouteDAO()
    {
        persistenceManager = PMF.get().getPersistenceManager();
    }

    /**
     * 
     * @param key
     * @return 
     */
    public Route get(Key key)
    {
        try
        {
            Route route = persistenceManager.getObjectById(Route.class, key);
            
            return persistenceManager.detachCopy(route);
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
    public List<Route> get()
    {
        String sql = "select from " + Route.class.getName();
        Query query = persistenceManager.newQuery(sql);

        return (List<Route>) query.execute();
    }

    /**
     * 
     * @param route 
     */
    public void save(Route route)
    {
        try
        {
            persistenceManager.makePersistent(route);
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
