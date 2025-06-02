package com.ensah.service;

import com.ensah.model.Inventaire;
import com.ensah.repository.InventaireRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventaireService {

    private final InventaireRepository inventaireRepository;

    public InventaireService(InventaireRepository inventaireRepository) {
        this.inventaireRepository = inventaireRepository;
    }

    public List<Inventaire> getAllInventaires() {
        return inventaireRepository.findAll();
    }

    public Inventaire getInventaireById(ObjectId id) {
        return inventaireRepository.findById(id).orElse(null);
    }

    public Inventaire saveInventaire(Inventaire inventaire) {
        return inventaireRepository.save(inventaire);
    }

    public void deleteInventaire(ObjectId id) {
        inventaireRepository.deleteById(id);
    }

    public List<Inventaire> findByEntrepot(ObjectId entrepotId) {
        return inventaireRepository.findAll().stream()
                .filter(i -> entrepotId.equals(i.getEntrepotId()))
                .toList();
    }
}