package com.ensah.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "sortie")
public abstract class Sortie {
    @Id
    private ObjectId id;
    private LocalDate dateSortie;
    private ObjectId produit;  // Utilisation de ObjectId pour le produit
    private int quantite;
    private String unite;
    private String type; // "commande" ou "divers"
    private ObjectId entrepot;  // Utilisation de ObjectId pour l'entrepôt

    public ObjectId getEntrepot() {
        return entrepot;
    }

    public void setEntrepot(ObjectId entrepot) {
        this.entrepot = entrepot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public ObjectId getProduit() {
        return produit;
    }

    public void setProduit(ObjectId produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }
}
