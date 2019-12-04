package org.tastefuljava.flipit.server;

import org.tastefuljava.flipit.data.Facet;
import org.tastefuljava.flipit.data.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
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

    private static Facet[] DEFAULT_FACETS = {
        new Facet("\uf79f", "Apéro"),
        new Facet("\uf7c9", "Ski"),
        new Facet("\uf7c5", "Patin"),
        new Facet("\uf45d", "Ping pong"),
        new Facet("\uf45f", "Volleyball"),
        new Facet("\uf6ec", "Randonnée"),
        new Facet("\uf188", "Debug"),
        new Facet("\uf7a6", "Guitare"),
        new Facet("\uf441", "Échecs"),
        new Facet("\uf206", "Cyclisme"),
        new Facet("\uf2b6", "Courier"),
        new Facet("\uf001", "Musique"),
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            User user;
            try (Persistence pm = Persistence.open()) {
                user = pm.getUser(request.getRemoteUser());
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
            response.setContentType("text/html;charset=UTF-8");
            try (final PrintWriter out = response.getWriter()) {
                JSon.write(user, out, true);
            }
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
            response.sendError(500);
        }
    }

    @Override
    public String getServletInfo() {
        return "FlipIt API";
    }
}
