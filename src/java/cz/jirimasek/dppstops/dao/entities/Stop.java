package cz.jirimasek.dppstops.dao.entities;

import com.google.appengine.api.datastore.Key;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * <code>Stop</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Stop
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String name;
    
    @Persistent
    private List<Key> lines;
    
    @Persistent
    private List<Key> platforms;

    public Key getKey()
    {
        return key;
    }

    public void setKey(Key key)
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Key> getLines()
    {
        return lines;
    }

    public void setLines(List<Key> lines)
    {
        this.lines = lines;
    }

    public List<Key> getPlatforms()
    {
        return platforms;
    }

    public void setPlatforms(List<Key> platforms)
    {
        this.platforms = platforms;
    }
}
