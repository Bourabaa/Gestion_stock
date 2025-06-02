package com.ensah.service;

import com.ensah.model.*;
import com.ensah.repository.EntrepotRepository;
import com.ensah.repository.ProduitRepository;
import com.ensah.repository.StockRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private EntrepotRepository entrepotRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock findById(ObjectId id) {
        return stockRepository.findById(id).orElse(null);
    }

    public void deleteById(ObjectId id) {
        Stock stock = findById(id);
        if (stock != null && stock.getQuantite() == 0) {
            stockRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Impossible de supprimer un stock non vide.");
        }
    }

    public double getQuantiteProduitDansEntrepot(String produitId, String entrepotId) {
        ObjectId produitObjectId = new ObjectId(produitId);
        ObjectId entrepotObjectId = new ObjectId(entrepotId);

        Stock stock = stockRepository.findByProduitIdAndEntrepotId(produitObjectId, entrepotObjectId);
        return (stock != null) ? stock.getQuantite() : 0;
    }

    public List<Produit> getProduitsParEntrepot(String entrepotId) {
        ObjectId entrepotObjectId = new ObjectId(entrepotId);
        List<Stock> stocks = stockRepository.findByEntrepotId(entrepotObjectId);

        return stocks.stream()
                .map(stock -> produitRepository.findById(stock.getProduitId()).orElse(null))
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    public List<Stock> getStocksParEntrepot(String entrepotId) {
        return stockRepository.findByEntrepotId(new ObjectId(entrepotId));
    }

    public List<Stock> getStocksParEntrepotNom(String entrepotNom) {
        Entrepot entrepot = entrepotRepository.findByNom(entrepotNom);
        if (entrepot != null) {
            return stockRepository.findByEntrepotId(entrepot.getId());
        }
        return new ArrayList<>();
    }

    public void enregistrerReception(Reception reception) {
        ObjectId produitId = reception.getProduitId();
        ObjectId entrepotId = reception.getEntrepotId();
        double quantiteReception = reception.getQuantite();
        String uniteReception = reception.getUnite();

        Produit produit = produitService.getProduitById(produitId);
        if (produit == null) throw new IllegalArgumentException("Produit introuvable");

        double quantiteBase = uniteReception.equals(produit.getUniteDeBase())
                ? quantiteReception
                : produit.convertirEnUniteDeBase(uniteReception, quantiteReception);

        Stock stock = stockRepository.findByProduitIdAndEntrepotId(produitId, entrepotId);
        if (stock != null) {
            stock.setQuantite(stock.getQuantite() + quantiteBase);
        } else {
            stock = new Stock();
            stock.setProduitId(produitId);
            stock.setEntrepotId(entrepotId);
            stock.setQuantite(quantiteBase);
            stock.setUnite(produit.getUniteDeBase());
        }

        stockRepository.save(stock);
    }

    public void ajusterStock(ObjectId produitId, ObjectId entrepotId, double quantite, String uniteBase) {
        Stock stock = stockRepository.findByProduitIdAndEntrepotId(produitId, entrepotId);

        if (stock == null) {
            stock = new Stock();
            stock.setProduitId(produitId);
            stock.setEntrepotId(entrepotId);
            stock.setUnite(uniteBase);
        }

        stock.setQuantite((int) quantite);
        stockRepository.save(stock);
    }

    public Stock findByProduitIdAndEntrepotId(ObjectId produitId, ObjectId entrepotId) {
        return stockRepository.findByProduitIdAndEntrepotId(produitId, entrepotId);
    }

    public void transfererProduit(Transfert transfert) {
        ObjectId produitId = transfert.getProduitId();
        Produit produit = produitService.getProduitById(produitId);
        if (produit == null) throw new IllegalArgumentException("Produit inexistant.");

        ObjectId sourceId = transfert.getEntrepotSource();
        ObjectId cibleId = transfert.getEntrepotDestiny();
        double quantite = transfert.getQuantite();
        String unite = transfert.getUnite();

        double quantiteBase = unite.equals(produit.getUniteDeBase())
                ? quantite
                : produit.convertirEnUniteDeBase(unite, quantite);

        Stock stockSource = stockRepository.findByProduitIdAndEntrepotId(produitId, sourceId);
        if (stockSource == null || stockSource.getQuantite() < quantiteBase) {
            throw new IllegalArgumentException("Stock insuffisant.");
        }

        stockSource.setQuantite(stockSource.getQuantite() - quantiteBase);
        stockRepository.save(stockSource);

        Stock stockCible = stockRepository.findByProduitIdAndEntrepotId(produitId, cibleId);
        if (stockCible != null) {
            stockCible.setQuantite(stockCible.getQuantite() + quantiteBase);
        } else {
            stockCible = new Stock();
            stockCible.setProduitId(produitId);
            stockCible.setEntrepotId(cibleId);
            stockCible.setQuantite(quantiteBase);
            stockCible.setUnite(produit.getUniteDeBase());
        }

        stockRepository.save(stockCible);
    }
}