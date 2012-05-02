package cz.jirimasek.dppstops.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <code>WebApplicationException</code>
 * 
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public abstract class WebApplicationException extends javax.ws.rs.WebApplicationException
{

    /**
     * Creates a new instance of <code>WebApplicationException</code> without detail message.
     */
    public WebApplicationException()
    {
    }

    /**
     * 
     * @param status
     */
    public WebApplicationException(Status status)
    {
        super(Response.status(status).build());
    }

    /**
     * 
     * @param status
     * @param message
     * @throws JSONException 
     */
    public WebApplicationException(Status status, String message) throws JSONException
    {
        super(Response.status(status)
                .entity(getEntity(message))
                .type(MediaType.APPLICATION_JSON).build());
    }

    /**
     * 
     * @param status
     * @param th
     * @throws JSONException 
     */
    public WebApplicationException(Status status, Throwable th) throws JSONException
    {
        super(Response.status(status)
                .entity(getEntity(th))
                .type(MediaType.APPLICATION_JSON).build());
    }

    /**
     * 
     * @param th
     * @return
     * @throws JSONException 
     */
    private static JSONObject getEntity(Throwable th) throws JSONException
    {
        String message = th.getMessage();
        String exception = th.getClass().getCanonicalName();

        String ex = String.format("%s: %s", exception, message);

        JSONObject entity = new JSONObject();

        entity.put("message", message);
        entity.put("exception", ex);

        return entity;
    }

    /**
     * 
     */
    private static JSONObject getEntity(String message) throws JSONException
    {
        JSONObject entity = new JSONObject();

        entity.put("message", message);

        return entity;
    }
}
