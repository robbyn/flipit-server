package org.tastefuljava.flipit.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.tastefuljava.flipit.server.Facet;
import org.tastefuljava.flipit.server.User;

public class Persistence implements AutoCloseable {
    private static final Logger LOG
            = Logger.getLogger(Persistence.class.getName());

    private final Connection cnt;

    public static Persistence open() {
        try {
            Context cxt = new InitialContext();
            DataSource ds = (DataSource)cxt.lookup("java:/comp/env/jdbc/flipit");
            return new Persistence(ds);
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
    }

    private Persistence(DataSource ds) {
        try {
            cnt = ds.getConnection();
            cnt.setAutoCommit(false);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            cnt.close();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
    }

    public void commit() {
        try {
            cnt.commit();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
    }

    public User getUser(String email) {
        User user;
        try (PreparedStatement stmt = cnt.prepareStatement(
                "select ID,EMAIL,PASSWORD_HASH,DISPLAY_NAME "
                + "from users where EMAIL=?")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                } else {
                    user = new User();
                    user.setId(rs.getInt(1));
                    user.setEmail(rs.getString(2));
                    user.setPasswordHash(rs.getString(3));
                    user.setDisplayName(rs.getString(4));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
        try (PreparedStatement stmt = cnt.prepareStatement(
                "select USER_ID,NUMBER,SYMBOL,LABEL "
                + "from facets where USER_ID=? order by USER_ID,NUMBER")) {
            stmt.setInt(1, user.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    user.addFacet(new Facet(
                            rs.getString(3),
                            rs.getString(4)));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
        return user;
    }

    public void updateFacets(User user) {
        try (PreparedStatement stmt = cnt.prepareStatement(
                "delete from facets where USER_ID=?")) {
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
        try (PreparedStatement stmt = cnt.prepareStatement(
                "insert into facets(USER_ID,NUMBER,SYMBOL,LABEL) "
                + "values(?,?,?,?)")) {
            int i = 0;
            for (Facet facet: user.getFacets()) {
                stmt.setInt(1, user.getId());
                stmt.setInt(2, i++);
                stmt.setString(3, facet.getSymbol());
                stmt.setString(4, facet.getLabel());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
    }
}
