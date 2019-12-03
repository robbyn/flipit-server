package org.tastefuljava.flipit.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.tastefuljava.flipit.persistence.Persistence;
import org.tastefuljava.jsonia.JSon;

@WebServlet(name = "ApiServlet", urlPatterns = {"/api/*"})
public class ApiServlet extends HttpServlet {
    private static final Logger LOG
            = Logger.getLogger(ApiServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Context cxt = new InitialContext();
            DataSource ds = (DataSource)cxt.lookup("java:/comp/env/jdbc/flipit");
            try (Persistence pm = new Persistence(ds)) {
            }
            response.setContentType("text/html;charset=UTF-8");
            try (final PrintWriter out = response.getWriter()) {
                JSon.write(new Object() {
                    String auth = "oauth";
                    int value = 123;
                }, out, true);
            }
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "FlipIt API";
    }
}
