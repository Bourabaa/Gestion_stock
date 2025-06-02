package com.ensah.model;

public class LigneInventaire {

    private String produit;
    private String unite;
    private double stockThéorique;
    private double stockRéel;
    private String justification;  // Nouveau champ

    // Getters et Setters
    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public double getStockThéorique() {
        return stockThéorique;
    }

    public void setStockThéorique(double stockThéorique) {
        this.stockThéorique = stockThéorique;
    }

    public double getStockRéel() {
        return stockRéel;
    }

    public void setStockRéel(double stockRéel) {
        this.stockRéel = stockRéel;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public double getEcart() {
        if (stockRéel == 0 && stockThéorique == 0) return 0;
        return stockRéel - stockThéorique;
    }
}