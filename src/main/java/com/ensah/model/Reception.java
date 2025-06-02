package com.ensah.model;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "receptions")
public class Reception {

    @Id
    private ObjectId id;

    @NotNull(message = "Le produit est obligatoire")
    private ObjectId produitId;

    @NotNull(message = "L'entrepôt est obligatoire")
    private ObjectId entrepotId;

    @NotNull(message = "La date de réception est obligatoire")
    private LocalDate dateReception;

    @NotBlank(message = "L'unité est obligatoire")
    private String unite;

    @Positive(message = "La quantité doit être un nombre positif")
    private double quantite;

    @NotBlank(message = "La source est obligatoire")
    private String source;

    private String remarque;

    @NotBlank(message = "Le numéro d'achat est obligatoire")
    private String numAchat;

    // Champs transitoires (non stockés en base)
    @Transient
    private Produit produit;

    @Transient
    private Entrepot entrepot;

    // === Getters et Setters ===

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

    public LocalDate getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDate dateReception) {
        this.dateReception = dateReception;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getNumAchat() {
        return numAchat;
    }

    public void setNumAchat(String numAchat) {
        this.numAchat = numAchat;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Entrepot getEntrepot() {
        return entrepot;
    }

    public void setEntrepot(Entrepot entrepot) {
        this.entrepot = entrepot;
    }
}
