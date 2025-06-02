package com.ensah.service;

import com.ensah.model.Produit;
import com.ensah.repository.ProduitRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Produit getProduitById(ObjectId id) {
        return produitRepository.findById(id).orElse(null);
    }

    public Produit getProduitParNom(String nom) {
        return produitRepository.findByNom(nom);
    }

    public Produit saveProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    public void deleteProduit(ObjectId id) {
        produitRepository.deleteById(id);
    }

    public double convertirVersUniteDeBase(ObjectId produitId, String unite, double quantite) {
        Produit produit = getProduitById(produitId);
        if (produit == null) throw new IllegalArgumentException("Produit introuvable");

        if (unite.equals(produit.getUniteDeBase())) {
            return quantite;
        }

        Double facteur = produit.getConversions().get(unite);
        if (facteur == null) throw new IllegalArgumentException("Unité invalide : " + unite);

        return quantite * facteur;
    }

    public double convertirDepuisUniteDeBase(ObjectId produitId, String uniteCible, double quantiteBase) {
        Produit produit = getProduitById(produitId);
        if (produit == null) throw new IllegalArgumentException("Produit introuvable");

        if (uniteCible.equals(produit.getUniteDeBase())) {
            return quantiteBase;
        }

        Double facteur = produit.getConversions().get(uniteCible);
        if (facteur == null || facteur == 0) throw new IllegalArgumentException("Unité cible invalide : " + uniteCible);

        return quantiteBase / facteur;
    }
}