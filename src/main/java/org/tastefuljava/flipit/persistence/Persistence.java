package org.tastefuljava.flipit.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.tastefuljava.flipit.server.Facet;
import org.tastefuljava.flipit.server.User;

public class Persistence implements AutoCloseable {
    private static final Logger LOG
            = Logger.getLogger(Persistence.class.getName());

    private final Connection cnt;

    public Persistence(DataSource ds) {
        try {
            this.cnt = ds.getConnection();
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
                            rs.getInt(2),
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
}
