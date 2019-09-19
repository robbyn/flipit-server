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
import org.tastefuljava.jsonia.JSon;

@WebServlet(name = "ApiServlet", urlPatterns = {"/api/*"})
public class ApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Context cxt = new InitialContext();
            DataSource ds = (DataSource)cxt.lookup("java:/comp/env/jdbc/flipit");
            try (Connection cnt = ds.getConnection()) {
                
            } catch (SQLException ex) {
                Logger.getLogger(ApiServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            response.setContentType("text/html;charset=UTF-8");
            try (final PrintWriter out = response.getWriter()) {
                JSon.write(new Object() {
                    String auth = "oauth";
                    int value = 123;
                }, out, true);
            }
        }   catch (NamingException ex) {
            Logger.getLogger(ApiServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (final PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ApiServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ApiServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public String getServletInfo() {
        return "FlipIt API";
    }
}
