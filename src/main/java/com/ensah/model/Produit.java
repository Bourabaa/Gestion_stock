package com.ensah.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.*;

@Document(collection = "products")
public class Produit {

    @Id
    private ObjectId id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La description  est obligatoire")
    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    @NotBlank(message = "La catégorie  est obligatoire")
    @Size(max = 100, message = "La catégorie ne doit pas dépasser 100 caractères")
    private String categorie;

    @NotBlank(message = "Le code Barre est obligatoire")
    @Size(max = 50, message = "Le code-barres ne doit pas dépasser 50 caractères")
    private String codeBarre;

    @NotBlank(message = "L’unité de base est obligatoire")
    private String uniteDeBase;

    @NotNull(message = "La liste des unités disponibles ne peut pas être nulle")
    private List<@NotBlank(message = "Chaque unité disponible doit être non vide") String> unitesDisponibles;

    @NotNull(message = "La map de conversions ne peut pas être nulle")
    private Map<
            @NotBlank(message = "Le nom de l’unité dans la conversion ne peut pas être vide") String,
            @NotNull(message = "Le facteur de conversion ne peut pas être nul") @Positive(message = "Le facteur de conversion doit être positif") Double
            > conversions;

    @Transient
    private String conversionsString;

    // --- GETTERS / SETTERS ---
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }

    public String getUniteDeBase() { return uniteDeBase; }
    public void setUniteDeBase(String uniteDeBase) { this.uniteDeBase = uniteDeBase; }

    public List<String> getUnitesDisponibles() { return unitesDisponibles; }
    public void setUnitesDisponibles(List<String> unitesDisponibles) { this.unitesDisponibles = unitesDisponibles; }

    public Map<String, Double> getConversions() { return conversions; }
    public void setConversions(Map<String, Double> conversions) { this.conversions = conversions; }

    public String getConversionsString() {
        if (conversions == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : conversions.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
        }
        return sb.toString().replaceAll(", $", "");
    }

    public void setConversionsString(String conversionsString) {
        this.conversionsString = conversionsString;
        if (conversionsString != null && !conversionsString.isBlank()) {
            Map<String, Double> map = new HashMap<>();
            String[] pairs = conversionsString.split(",");
            for (String pair : pairs) {
                String[] kv = pair.trim().split(":");
                if (kv.length == 2) {
                    map.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
                }
            }
            this.conversions = map;
        }
    }

    public double convertirEnUniteDeBase(String unite, double quantite) {
        if (unite.equals(uniteDeBase)) return quantite;
        Double facteur = conversions.get(unite);
        if (facteur == null) throw new IllegalArgumentException("Unité inconnue : " + unite);
        return quantite * facteur;
    }

    public double convertirDepuisUniteDeBase(String uniteCible, double quantiteBase) {
        if (uniteCible.equals(uniteDeBase)) return quantiteBase;
        Double facteur = conversions.get(uniteCible);
        if (facteur == null || facteur == 0)
            throw new IllegalArgumentException("Unité cible invalide : " + uniteCible);
        return quantiteBase / facteur;
    }
}
