package org.tastefuljava.flipit.server;

import java.io.BufferedReader;
import org.tastefuljava.flipit.data.Facet;
import org.tastefuljava.flipit.data.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tastefuljava.flipit.persistence.Persistence;
import org.tastefuljava.jsonia.JSon;

@WebServlet(name = "UserServlet", urlPatterns = {"/api/user/*"})
public class UserServlet extends HttpServlet {
    private static final Logger LOG
            = Logger.getLogger(UserServlet.class.getName());

    private static final Facet[] DEFAULT_FACETS = {
        new Facet("\uf000", "Apéro"),
        new Facet("\uf083", "Photographie"),
        new Facet("\uf008", "Cinéma"),
        new Facet("\uf45d", "Ping pong"),
        new Facet("\uf45f", "Volleyball"),
        new Facet("\uf1e3", "Football"),
        new Facet("\uf188", "Debug"),
        new Facet("\uf075", "Discussion"),
        new Facet("\uf441", "Échecs"),
        new Facet("\uf206", "Cyclisme"),
        new Facet("\uf2b6", "Courier"),
        new Facet("\uf001", "Musique"),
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            User user;
            try (Persistence pm = Persistence.open()) {
                user = pm.getUser(req.getRemoteUser());
                if (path == null) {
                } else if (path.equals("/default-facets")) {
                    user.clearFacets();
                    for (Facet facet: DEFAULT_FACETS) {
                        user.addFacet(facet);
                    }
                    pm.updateFacets(user);
                    pm.commit();
                }
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try (final PrintWriter out = resp.getWriter()) {
                JSon.write(user, out, true);
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            User user;
            try (Persistence pm = Persistence.open()) {
                user = pm.getUser(req.getRemoteUser());
                if (path == null) {
                } else if (path.equals("/facets")) {
                    try (BufferedReader in = req.getReader()) {
                        Facet[] facets = JSon.read(in, Facet[].class);
                        user.clearFacets();
                        for (Facet facet: facets) {
                            user.addFacet(facet);
                        }
                    }
                    pm.updateFacets(user);
                    pm.commit();
                }
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try (final PrintWriter out = resp.getWriter()) {
                JSon.write(user, out, true);
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
        return "FlipIt User API";
    }
}
