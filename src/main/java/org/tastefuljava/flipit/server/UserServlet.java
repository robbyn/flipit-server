package org.tastefuljava.flipit.server;

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

@WebServlet(name = "UserServlet", urlPatterns = {"/api/user"})
public class UserServlet extends HttpServlet {
    private static final Logger LOG
            = Logger.getLogger(UserServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user;
            try (Persistence pm = Persistence.open()) {
                user = pm.getUser(request.getRemoteUser());
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
