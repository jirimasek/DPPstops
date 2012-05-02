package cz.jirimasek.dppstops;

import cz.jirimasek.dppstops.common.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>InitializerServlet</code>
 *
 * @author Jiří Mašek <email@jirimasek.cz>
 */
public class InitializerServlet extends HttpServlet
{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        Initializer initializer = new Initializer();

        String arg = req.getParameter("arg");

        if (arg.equals("lines"))
        {
            initializer.initializeLines();
        }
        else if (arg.equals("stops"))
        {
            String letter = req.getParameter("letter");

            if (StringUtils.isNullOrEmpty(letter))
            {
                initializer.initializeStops();
            }
            else
            {
                initializer.initializeStops(letter);
            }
        }
        else if (arg.equals("delete-lines"))
        {
            initializer.deleteLines();
        }
        else if (arg.equals("delete-stops"))
        {
            initializer.deleteStops();
        }
    }
}
