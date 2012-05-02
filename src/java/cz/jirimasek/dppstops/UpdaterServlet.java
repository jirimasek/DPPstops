package cz.jirimasek.dppstops;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>UpdaterServlet</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class UpdaterServlet extends HttpServlet
{
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        
        Updater extractor = new Updater();
        
        extractor.run();
    }
    
}
