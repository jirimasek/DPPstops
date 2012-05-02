package cz.jirimasek.dppstops.exceptions;

import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;

/**
 * <code>BadRequestException</code>
 * 
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class BadRequestException extends WebApplicationException
{

    /**
     * Creates a new instance of <code>BadRequestException</code> without detail message.
     */
    public BadRequestException()
    {
        super(Response.Status.BAD_REQUEST);
    }

    /**
     * 
     * @param message
     * @throws JSONException 
     */
    public BadRequestException(String message) throws JSONException
    {
        super(Response.Status.BAD_REQUEST, message);
    }

    /**
     * 
     * @param th
     * @throws JSONException 
     */
    public BadRequestException(Throwable th) throws JSONException
    {
        super(Response.Status.BAD_REQUEST, th);
    }
}
