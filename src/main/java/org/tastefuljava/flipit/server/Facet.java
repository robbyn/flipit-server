package org.tastefuljava.flipit.server;

public class Facet {
    private int number;
    private String symbol;
    private String label;

    public Facet() {
    }

    public Facet(int number, String symbol, String label) {
        this.number = number;
        this.symbol = symbol;
        this.label = label;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
