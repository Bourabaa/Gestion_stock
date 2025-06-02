package com.ensah.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Document(collection = "inventaires")
public class Inventaire {

    @Id
    private ObjectId id;

    private LocalDate dateInventaire;
    private ObjectId entrepotId;
    private List<LigneInventaire> lignes;
    private boolean valide;
    private String remarque;
    private String effectuePar;
    private String validePar;



    // Getters et Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public LocalDate getDateInventaire() { return dateInventaire; }
    public void setDateInventaire(LocalDate dateInventaire) { this.dateInventaire = dateInventaire; }

    public ObjectId getEntrepotId() { return entrepotId; }
    public void setEntrepotId(ObjectId entrepot) { this.entrepotId = entrepot; }

    public List<LigneInventaire> getLignes() { return lignes; }
    public void setLignes(List<LigneInventaire> lignes) { this.lignes = lignes; }

    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }

    public String getEffectuePar() { return effectuePar; }
    public void setEffectuePar(String effectuePar) { this.effectuePar = effectuePar; }

    public String getValidePar() { return validePar; }
    public void setValidePar(String validePar) { this.validePar = validePar; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }


    public double getEcartTotal() {
        if (lignes == null) return 0;

        return lignes.stream()
                .filter(Objects::nonNull)
                .mapToDouble(l -> {
                    try {
                        return l.getEcart(); // méthode dans LigneInventaire
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
    }
}
