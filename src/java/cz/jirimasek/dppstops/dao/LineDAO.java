package cz.jirimasek.dppstops.dao;

import com.google.appengine.api.datastore.Key;
import cz.jirimasek.dppstops.dao.entities.Line;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * <code></code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class LineDAO
{

    private PersistenceManager persistenceManager;

    /**
     * Vytvoří novou instanci třídy <code>LineDAO</code>.
     */
    public LineDAO()
    {
        persistenceManager = PMF.get().getPersistenceManager();
    }

    /**
     * 
     * @param key
     * @return 
     */
    public Line get(Key key)
    {
        try
        {
            Line line = persistenceManager.getObjectById(Line.class, key);
            
            return persistenceManager.detachCopy(line);
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
    public List<Line> get()
    {
        String sql = "select from " + Line.class.getName();
        Query query = persistenceManager.newQuery(sql);

        return (List<Line>) query.execute();
    }

    /**
     * 
     * @param line 
     */
    public void save(Line line)
    {
        try
        {
            persistenceManager.makePersistent(line);
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
