package com.ensah.service;

import com.ensah.model.Entrepot;
import com.ensah.model.Produit;
import com.ensah.model.Reception;
import com.ensah.model.Stock;
import com.ensah.repository.EntrepotRepository;
import com.ensah.repository.ProduitRepository;
import com.ensah.repository.ReceptionRepository;
import com.ensah.repository.StockRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Service
public class ReceptionService {

    @Autowired
    private ReceptionRepository receptionRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private EntrepotRepository entrepotRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Reception> getAllReceptions() {
        List<Reception> receptions = receptionRepository.findAll();

        for (Reception r : receptions) {
            if (r.getProduitId() != null) {
                Produit p = produitRepository.findById(r.getProduitId()).orElse(null);
                r.setProduit(p);
            }

            if (r.getEntrepotId() != null) {
                Entrepot e = entrepotRepository.findById(r.getEntrepotId()).orElse(null);
                r.setEntrepot(e);
            }
        }

        return receptions;
    }

    public Reception getReceptionById(ObjectId id) {
        return receptionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Reception saveReception(Reception reception) {
        if (!produitRepository.existsById(reception.getProduitId()) ||
                !entrepotRepository.existsById(reception.getEntrepotId())) {
            throw new IllegalArgumentException("Produit ou entrepôt introuvable.");
        }

        // Conversion de la quantité réceptionnée vers unité de base
        double quantiteConvertie = produitService.convertirVersUniteDeBase(
                reception.getProduitId(),
                reception.getUnite(),
                reception.getQuantite()
        );

        // Enregistrement de la réception
        Reception savedReception = receptionRepository.save(reception);

        // Mise à jour du stock
        Stock existingStock = stockRepository.findByProduitIdAndEntrepotId(
                reception.getProduitId(), reception.getEntrepotId());

        if (existingStock != null) {
            existingStock.setQuantite(existingStock.getQuantite() + (int)quantiteConvertie);
            stockRepository.save(existingStock);
        } else {
            Stock newStock = new Stock();
            newStock.setProduitId(reception.getProduitId());
            newStock.setEntrepotId(reception.getEntrepotId());
            newStock.setQuantite((int)quantiteConvertie);
            newStock.setUnite(produitService.getProduitById(reception.getProduitId()).getUniteDeBase());
            stockRepository.save(newStock);
        }

        return savedReception;
    }

    public void deleteReception(ObjectId id) {
        receptionRepository.deleteById(id);
    }

//    public List<Reception> filtrerReceptions(String dateDebut, String dateFin, ObjectId produitId, ObjectId entrepotId) {
//        Query query = new Query();
//
//        if (dateDebut != null && !dateDebut.isEmpty() && dateFin != null && !dateFin.isEmpty()) {
//            query.addCriteria(Criteria.where("dateReception").gte(dateDebut).lte(dateFin));
//        }
//
//        if (produitId != null) {
//            query.addCriteria(Criteria.where("produitId").is(produitId));
//        }
//
//        if (entrepotId != null) {
//            query.addCriteria(Criteria.where("entrepotId").is(entrepotId));
//        }
//
//        return mongoTemplate.find(query, Reception.class);
//    }
    public List<Reception> filtrerReceptions(String dateDebut, String dateFin, ObjectId produitId, ObjectId entrepotId) {
        Query query = new Query();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (dateDebut != null && !dateDebut.isEmpty() && dateFin != null && !dateFin.isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(dateDebut, formatter);
                LocalDate end = LocalDate.parse(dateFin, formatter);

                // Convertir en Date pour MongoDB (java.util.Date)
                Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()); // inclure toute la journée

                query.addCriteria(Criteria.where("dateReception").gte(startDate).lt(endDate));
            } catch (DateTimeParseException e) {
                // Log si parsing échoue
                System.out.println("Erreur de parsing de date : " + e.getMessage());
            }
        }

        if (produitId != null) {
            query.addCriteria(Criteria.where("produitId").is(produitId));
        }

        if (entrepotId != null) {
            query.addCriteria(Criteria.where("entrepotId").is(entrepotId));
        }


        return mongoTemplate.find(query, Reception.class);
    }

}
