package com.ensah.model;

public class SortieDivers extends Sortie {
    private String motif; // Ex : "Don", "Perte", "Inventaire"
    private String description;

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
