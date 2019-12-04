package org.tastefuljava.flipit.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tastefuljava.flipit.data.Activity;
import org.tastefuljava.flipit.data.User;
import org.tastefuljava.flipit.persistence.Persistence;
import org.tastefuljava.jsonia.JSon;

@WebServlet(name = "ActivityServlet", urlPatterns = {"/api/activity/*"})
public class ActivityServlet extends HttpServlet {
    private static final Logger LOG
            = Logger.getLogger(ActivityServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null) {
                try (Persistence pm = Persistence.open()) {
                    User user = pm.getUser(req.getRemoteUser());
                    Date from = Util.parseDate(req.getParameter("from"));
                    Date to = Util.parseDate(req.getParameter("to"));
                    if (to == null) {
                        to = new Date();
                    }
                    if (from == null) {
                        from = Util.addDays(to, -1);
                    }
                    List<Activity> activities
                            = pm.queryActivities(user, from, to);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    try (final PrintWriter out = resp.getWriter()) {
                        JSon.write(user, out, true);
                    }
                }
            } else {
                LOG.log(Level.SEVERE, "Unknown method {0}", path);
                resp.sendError(404);
            }
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
            resp.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    @Override
    public String getServletInfo() {
        return "FlipIt Activity API";
    }
}
