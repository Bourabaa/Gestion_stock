package com.ensah.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Document
public class Transfert {
    @Id
    private ObjectId id;
    private LocalDate dateTransfert;
    private ObjectId produitId;
    private String unite;
    private double quantite;
    private ObjectId entrepotSource;
    private ObjectId entrepotDestiny;

    @Transient
    private Produit produit;

    @Transient
    private Entrepot entrepot;

    @Transient
    private String entrepotSourceNom;

    @Transient
    private String entrepotDestinyNom;

    public String getEntrepotSourceNom() {
        return entrepotSourceNom;
    }

    public void setEntrepotSourceNom(String nom) {
        this.entrepotSourceNom = nom;
    }

    public String getEntrepotDestinyNom() {
        return entrepotDestinyNom;
    }

    public void setEntrepotDestinyNom(String nom) {
        this.entrepotDestinyNom = nom;
    }

    public void setEntrepot(Entrepot entrepot) {
        this.entrepot = entrepot;
    }

    public void setProduit(Produit produit) { this.produit = produit;}

    public Produit getProduit() {
        return produit;
    }

    public Entrepot getEntrepot() {return entrepot;}





    public ObjectId getProduitId() {
        return produitId;
    }

    public void setProduitId(ObjectId produit) {
        this.produitId = produit;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public ObjectId getEntrepotSource() {
        return entrepotSource;
    }

    public void setEntrepotSource(Entrepot entrepotSource) {
        this.entrepotSource = entrepotSource.getId();
    }

    public ObjectId getEntrepotDestiny() {
        return entrepotDestiny;
    }

    public void setEntrepotDestiny(Entrepot entrepotDestiny) {
        this.entrepotDestiny = entrepotDestiny.getId();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDate getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(LocalDate dateTransfert) {
        this.dateTransfert = dateTransfert;
    }
}
