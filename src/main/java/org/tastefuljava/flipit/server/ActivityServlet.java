package org.tastefuljava.flipit.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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
                query(req, resp);
            } else if (path.equals("/log")) {
                log(req, resp);
            } else if (path.equals("/last")) {
                last(req, resp);
            } else {
                LOG.log(Level.SEVERE, "Unknown method {0}", path);
                resp.sendError(404);
            }
        } catch (HttpException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            resp.sendError(ex.getStatus());
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
            resp.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null) {
                query(req, resp);
            } else if (path.equals("/log")) {
                log(req, resp);
            } else {
                LOG.log(Level.SEVERE, "Unknown method {0}", path);
                resp.sendError(404);
            }
        } catch (HttpException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            resp.sendError(ex.getStatus());
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
            resp.sendError(500);
        }
    }

    @Override
    public String getServletInfo() {
        return "FlipIt Activity API";
    }

    private void query(HttpServletRequest req, HttpServletResponse resp)
            throws ParseException, IOException {
        try (Persistence pm = Persistence.open()) {
            User user = pm.getUser(req.getRemoteUser());
            Date from = Util.parseDate(req.getParameter("from"));
            Date to = Util.parseDate(req.getParameter("to"));
            if (to == null) {
                to = new Date();
            }
            if (from == null) {
                from = Util.startOfDay(to);
            }
            List<Activity> activities
                    = pm.queryActivities(user, from, to);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try (final PrintWriter out = resp.getWriter()) {
                JSon.write(activities, out, true);
            }
        }
    }

    private void log(HttpServletRequest req, HttpServletResponse resp) {
        try (Persistence pm = Persistence.open()) {
            Activity act = new Activity();
            User user = pm.getUser(req.getRemoteUser());
            String s = req.getParameter("facet");
            if (Util.isBlank(s)) {
                throw new HttpException(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request: facet is null");
            }
            int facetNumber = Integer.parseInt(s);
            act.setFacetNumber(facetNumber);
            act.setStartTime(new Date());
            s = req.getParameter("comment");
            act.setComment(s);
            pm.logActivity(user, act);
            pm.commit();
        }
    }

    private void last(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try (Persistence pm = Persistence.open()) {
            User user = pm.getUser(req.getRemoteUser());
            Activity act = pm.lastActivity(user);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try (final PrintWriter out = resp.getWriter()) {
                JSon.write(act, out, true);
            }
        }
    }
}
