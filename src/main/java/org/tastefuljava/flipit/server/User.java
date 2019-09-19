package org.tastefuljava.flipit.server;

import java.util.ArrayList;
import java.util.List;

public class User {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
