package com.ensah.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stocks")
public class Stock {

    @Id
    private ObjectId id;


    private ObjectId produitId;  // Référence à l'ID du produit
    private ObjectId entrepotId;  // Référence à l'ID de l'entrepôt
    private double quantite;         // Quantité du produit dans l'entrepôt
    private String unite;         // Unité du produit (ex: kg, litre)


    @Transient
    private Produit produit;

    @Transient
    private Entrepot entrepot;

    public void setEntrepot(Entrepot entrepot) {
        this.entrepot = entrepot;
    }
    public void setProduit(Produit produit) { this.produit = produit;}

    public Produit getProduit() {
        return produit;
    }

    public Entrepot getEntrepot() {return entrepot;}


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getProduitId() {
        return produitId;
    }

    public void setProduitId(ObjectId produitId) {
        this.produitId = produitId;
    }

    public ObjectId getEntrepotId() {
        return entrepotId;
    }

    public void setEntrepotId(ObjectId entrepotId) {
        this.entrepotId = entrepotId;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }



}
