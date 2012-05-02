package cz.jirimasek.dppstops.dao.entities;

import com.google.appengine.api.datastore.Key;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * <code>Line</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Line
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String number;
    
    @Persistent
    private String transport;
    
    @Persistent
    private List<Key> routes;

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
    public String getNumber()
    {
        return number;
    }

    /**
     * 
     * @param number 
     */
    public void setNumber(String number)
    {
        this.number = number;
    }

    /**
     * 
     * @return 
     */
    public String getTransport()
    {
        return transport;
    }

    /**
     * 
     * @param transport 
     */
    public void setTransport(String transport)
    {
        this.transport = transport;
    }

    /**
     * 
     * @return 
     */
    public List<Key> getRoutes()
    {
        return routes;
    }

    /**
     * 
     * @param routes 
     */
    public void setRoutes(List<Key> routes)
    {
        this.routes = routes;
    }
}
