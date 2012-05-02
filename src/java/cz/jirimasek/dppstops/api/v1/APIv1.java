package cz.jirimasek.dppstops.api.v1;

import cz.jirimasek.dppstops.api.v1.entities.JSONBuilder;
import cz.jirimasek.dppstops.common.StringUtils;
import cz.jirimasek.dppstops.exceptions.BadRequestException;
import cz.jirimasek.dppstops.exceptions.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
@Path("v1")
public class APIv1
{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ApiResource
     */
    public APIv1()
    {
    }

    @GET
    @Path("stops")
    @Produces("application/json")
    public JSONObject retrieveStops(@QueryParam("lat") String lat,
            @QueryParam("lng") String lng, @QueryParam("dist") String dist)
            throws JSONException
    {
        if (StringUtils.isNullOrEmpty(lat) ^ StringUtils.isNullOrEmpty(lng))
        {
            throw new BadRequestException(
                    "There must be set both coordinates or none of them.");
        }
        
        JSONBuilder jb = new JSONBuilder();

        if (!StringUtils.isNullOrEmpty(lat) && !StringUtils.isNullOrEmpty(lng))
        {
            try
            {
                double latitude = Integer.parseInt(lat) * 1E-6;
                double longitude = Integer.parseInt(lng) * 1E-6;
                
                int distance = StringUtils.isNullOrEmpty(dist) ? 150 : Integer.parseInt(dist);
                
                return jb.getPlatforms(latitude, longitude, distance);
                
            }
            catch (NumberFormatException ex)
            {
                throw new BadRequestException(ex);
            }

        }

        return jb.getStops();
    }

    @GET
    @Path("lines")
    @Produces("application/json")
    public JSONObject retrieveLines() throws JSONException
    {
        JSONBuilder jb = new JSONBuilder();

        return jb.getLines();
    }
    
    @GET
    @Path("lines/{number}")
    @Produces("application/json")
    public JSONObject retrieveLine(@PathParam("number") String number) throws JSONException
    {
        JSONBuilder jb = new JSONBuilder();
        
        JSONObject line = jb.getLine(number);
        
        if (line == null)
        {
            throw new NotFoundException("Line not found.");
        }

        return line;
    }
}
