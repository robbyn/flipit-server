package org.tastefuljava.flipit.server;

public class Facet {
    public final int number;
    public final String symbol;
    public final String label;

    @Deprecated
    public Facet() {
        this(0, null, null);
    }

    public Facet(int number, String symbol, String label) {
        this.number = number;
        this.symbol = symbol;
        this.label = label;
    }
}
