package cz.jirimasek.dppstops.exceptions;

import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;

/**
 * <code>NotFoundException</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class NotFoundException extends WebApplicationException
{

    /**
     * Creates a new instance of <code>NotFoundException</code> without detail message.
     */
    public NotFoundException()
    {
        super(Response.Status.NOT_FOUND);
    }

    /**
     * 
     * @param message
     * @throws JSONException 
     */
    public NotFoundException(String message) throws JSONException
    {
        super(Response.Status.NOT_FOUND, message);
    }

    /**
     * 
     * @param th
     * @throws JSONException 
     */
    public NotFoundException(Throwable th) throws JSONException
    {
        super(Response.Status.NOT_FOUND, th);
    }
}
