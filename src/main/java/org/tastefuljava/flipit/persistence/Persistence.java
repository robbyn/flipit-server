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
    private static final Logger LOG = Logger.getLogger(Persistence.class.getName());

    public User getUser(String email) {
        try (PreparedStatement stmt = cnt.prepareStatement(
                "select ID,EMAIL,PASSWORD_HASH,DISPLAY_NAME,NUMBER,SYMBOL,LABEL "
                + "from users left outer join facets on ID=USER_ID "
                + "where EMAIL=?")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                } else {
                    User user = new User();
                    user.setId(rs.getInt(1));
                    user.setEmail(rs.getString(2));
                    user.setPasswordHash(rs.getString(3));
                    user.setDisplayName(rs.getString(4));
                    Integer number = rs.getObject(5, Integer.class);
                    String symbol = rs.getString(6);
                    String label = rs.getString(7);
                    if (number != null) {
                        user.addFacet(new Facet(number, symbol, label));
                        while (rs.next()) {
                            if (rs.getInt(1) != user.getId()) {
                                break;
                            }
                            user.addFacet(new Facet(
                                    rs.getInt(5),
                                    rs.getString(6),
                                    rs.getString(7)));
                        }
                    }
                    return user;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new PersistenceException(ex.getMessage());
        }
    }
}
