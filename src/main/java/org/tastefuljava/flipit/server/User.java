package org.tastefuljava.flipit.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private static final Charset DIGEST_ENCODING = StandardCharsets.UTF_8;
    private static final String DIGEST_ALGORITHM = "SHA-256";

    private int id;
    private String email;
    private String passwordHash;
    private String displayName;
    private final List<Facet> facets = new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        try {
            byte[] bytes = password.getBytes(DIGEST_ENCODING);
            this.passwordHash = Util.hex(Util.hash(DIGEST_ALGORITHM, bytes));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Facet> getFacets() {
        return facets;
    }
}
