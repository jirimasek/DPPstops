package cz.jirimasek.dppstops.common;

/**
 * <code>GPSUtils</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class GPSUtils
{

    /**
     * 
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 
     */
    public static double distFrom(double lat1, double lng1, double lat2,
            double lng2)
    {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLon = Math.sin(dLon / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLon, 2) * Math.cos(lat1) * Math.
                cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }
    
    /**
     * 
     * @param miles
     * @return 
     */
    public static int miles2meters(double miles)
    {
        int meters = (int) Math.round(miles * 1609.344);
  
        return meters;
    }
}
