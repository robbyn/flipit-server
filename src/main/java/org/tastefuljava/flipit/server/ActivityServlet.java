package org.tastefuljava.flipit.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
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
import org.tastefuljava.flipit.data.Facet;
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
            } else if (path.equals("/summary")) {
                summary(req, resp);
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
                    = pm.queryActivities(user, from, to, true);
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

    private void summary(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ParseException {
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
            List<Facet> facets = user.getFacets();
            long[] total = new long[facets.size()];
            List<Activity> activities
                    = pm.queryActivities(user, from, to, false);
            Activity last = null;
            for (Activity current: activities) {
                int facetNumber = current.getFacetNumber();
                if (facetNumber >= 0 && facetNumber < total.length) {
                    if (last != null && last.getFacetNumber() != facetNumber) {
                        total[last.getFacetNumber()]
                                += current.getStartTime().getTime()
                                - last.getStartTime().getTime();
                    }
                    last = current;
                }
            }
            if (last != null) {
                total[last.getFacetNumber()]
                        += to.getTime() - last.getStartTime().getTime();
            }
            List<String> result = new ArrayList<>();
            for (long sum: total) {
                result.add(formatMillis(sum));
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try (final PrintWriter out = resp.getWriter()) {
                JSon.write(result, out, true);
            }
        }
    }

    private static final String formatMillis(long delta) {
        if (delta == 0) {
            return "";
        }
        int millis = (int)(delta%1000);
        delta /= 1000;
        int secs = (int)(delta%60);
        delta /= 60;
        int mins = (int)(delta%60);
        delta /= 60;
        int hours = (int)(delta%24);
        delta /= 24;
        int days = (int)delta;
        StringBuilder buf = new StringBuilder();
        if (days != 0) {
            buf.append(days);
            buf.append(' ');
        }
        buf.append(String.format("%02d:%02d:%02d", hours, mins, secs));
        return buf.toString();
    }
}
