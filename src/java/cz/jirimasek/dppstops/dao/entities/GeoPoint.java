package cz.jirimasek.dppstops.dao.entities;

import com.google.appengine.api.datastore.Key;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * <code>GeoPoint</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GeoPoint
{

    private static final double MIN_LAT = Math.toRadians(-90d);
    private static final double MAX_LAT = Math.toRadians(90d);
    private static final double MIN_LON = Math.toRadians(-180d);
    private static final double MAX_LON = Math.toRadians(180d);
    
    public static double EARTH_RADIUS = 6371.01;
    ;
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    //* 1E6
    @Persistent
    private Integer latitude;
    @Persistent
    private Integer longitude;

    public Key getKey()
    {
        return key;
    }

    public void setKey(Key key)
    {
        this.key = key;
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

    public GeoPoint[] boundingCoordinates(double distance)
    {

        if (distance < 0d)
        {
            throw new IllegalArgumentException();
        }

        double lat = Math.toRadians(latitude * 1E-6);
        double lng = Math.toRadians(longitude * 1E-6);

        double radDist = distance / EARTH_RADIUS;

        double minLat = lat - radDist;
        double maxLat = lat + radDist;

        double minLon;
        double maxLon;

        if (minLat > MIN_LAT && maxLat < MAX_LAT)
        {
            double deltaLon = Math.asin(Math.sin(radDist)
                    / Math.cos(lat));
            minLon = lng - deltaLon;
            
            if (minLon < MIN_LON)
            {
                minLon += 2d * Math.PI;
            }
            
            maxLon = lng + deltaLon;
            
            if (maxLon > MAX_LON)
            {
                maxLon -= 2d * Math.PI;
            }
        }
        else
        {
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }
        
        GeoPoint minGeoPoint = new GeoPoint();
        
        minGeoPoint.setLatitude((int) (Math.toDegrees(minLat) * 1E6));
        minGeoPoint.setLongitude((int) (Math.toDegrees(minLon) * 1E6));
        
        GeoPoint maxGeoPoint = new GeoPoint();
        
        maxGeoPoint.setLatitude((int) (Math.toDegrees(maxLat) * 1E6));
        maxGeoPoint.setLongitude((int) (Math.toDegrees(maxLon) * 1E6));
        
        GeoPoint[] boundaries = new GeoPoint[2];
        
        boundaries[0] = minGeoPoint;
        boundaries[1] = maxGeoPoint;
        
        return boundaries;
    }
}
