package cz.jirimasek.dppstops.dao.entities;

import com.google.appengine.api.datastore.Key;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * <code>Platform</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Platform
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private Key stop;
    
    @Persistent
    private String transport;
    //* 1E6
    @Persistent
    private Integer latitude;
    
    @Persistent
    private Integer longitude;
    
    @Persistent
    private List<Key> lines;

    public Key getKey()
    {
        return key;
    }

    public void setKey(Key key)
    {
        this.key = key;
    }

    public Key getStop()
    {
        return stop;
    }

    public void setStop(Key stop)
    {
        this.stop = stop;
    }

    public String getTransport()
    {
        return transport;
    }

    public void setTransport(String type)
    {
        this.transport = type;
    }

    public Integer getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Integer latitude)
    {
        this.latitude = latitude;
    }

    public Integer getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Integer longitude)
    {
        this.longitude = longitude;
    }

    public List<Key> getLines()
    {
        return lines;
    }

    public void setLines(List<Key> lines)
    {
        this.lines = lines;
    }
}
