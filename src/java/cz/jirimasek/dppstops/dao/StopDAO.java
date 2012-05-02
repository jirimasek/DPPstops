package cz.jirimasek.dppstops.dao;

import com.google.appengine.api.datastore.Key;
import cz.jirimasek.dppstops.dao.entities.Stop;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * <code>StopDAO</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class StopDAO
{

    private PersistenceManager persistenceManager;

    /**
     * Vytvoří novou instanci třídy <code>StopDAO</code>.
     */
    public StopDAO()
    {
        persistenceManager = PMF.get().getPersistenceManager();
    }

    /**
     * 
     * @param key
     * @return 
     */
    public Stop get(Key key)
    {
        try
        {
            Stop stop = persistenceManager.getObjectById(Stop.class, key);
            
            return persistenceManager.detachCopy(stop);
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
    public List<Stop> get()
    {
        String sql = "select from " + Stop.class.getName();
        Query query = persistenceManager.newQuery(sql);

        return (List<Stop>) query.execute();
    }

    /**
     * 
     * @param stop 
     */
    public void save(Stop stop)
    {
        try
        {
            persistenceManager.makePersistent(stop);
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
