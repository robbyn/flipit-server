package org.tastefuljava.flipit.data;

import java.util.Date;

public class Activity {
    private User user;
    private int facetNumber;
    private Date startTime;
    private String comment;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Facet getFacet() {
        if (user == null) {
            return null;
        } else {
            return user.getFacet(facetNumber);
        }
    }

    public int getFacetNumber() {
        return facetNumber;
    }

    public void setFacetNumber(int facetNumber) {
        this.facetNumber = facetNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
