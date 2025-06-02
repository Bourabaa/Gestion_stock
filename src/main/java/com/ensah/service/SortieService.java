package com.ensah.service;

import com.ensah.model.Sortie;
import com.ensah.model.Stock;
import com.ensah.repository.EntrepotRepository;
import com.ensah.repository.ProduitRepository;
import com.ensah.repository.SortieRepository;
import com.ensah.repository.StockRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SortieService {

    @Autowired
    private SortieRepository sortieRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private EntrepotRepository entrepotRepository;

    @Autowired
    private StockRepository stockRepository;

    // Méthode pour enregistrer une sortie
    @Transactional
    public Sortie saveSortie(Sortie sortie) {
        // Vérification de l'existence du produit
        if (!produitRepository.existsById(sortie.getProduit())) {
            throw new IllegalArgumentException("Produit introuvable.");
        }

        // Vérification de l'existence de l'entrepôt
        if (!entrepotRepository.existsById(sortie.getEntrepot())) {
            throw new IllegalArgumentException("Entrepôt introuvable.");
        }

        // Vérification de la quantité en stock
        Stock existingStock = stockRepository.findByProduitIdAndEntrepotId(sortie.getProduit(), sortie.getEntrepot());
        if (existingStock == null || existingStock.getQuantite() < sortie.getQuantite()) {
            throw new IllegalArgumentException("Quantité insuffisante en stock.");
        }

        // Mise à jour du stock (décrémentation)
        existingStock.setQuantite(existingStock.getQuantite() - sortie.getQuantite());
        stockRepository.save(existingStock);

        // Enregistrement de la sortie
        return sortieRepository.save(sortie);
    }

    public List<Sortie> findAll() {
        return sortieRepository.findAll();
    }

    public Sortie findById(ObjectId id) {
        return sortieRepository.findById(id).orElse(null);
    }

    public void delete(ObjectId id) {
        sortieRepository.deleteById(id);
    }
}
