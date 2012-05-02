package cz.jirimasek.dppstops.dao.entities;

import com.google.appengine.api.datastore.Key;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * <code>Route</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Route
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private List<Key> stops;

    /**
     * 
     * @return 
     */
    public Key getKey()
    {
        return key;
    }

    /**
     * 
     * @param key 
     */
    public void setKey(Key key)
    {
        this.key = key;
    }

    /**
     * 
     * @return 
     */
    public List<Key> getStops()
    {
        return stops;
    }

    /**
     * 
     * @param stops 
     */
    public void setStops(List<Key> stops)
    {
        this.stops = stops;
    }    
}
