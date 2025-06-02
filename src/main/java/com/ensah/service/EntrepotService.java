package com.ensah.service;

import com.ensah.model.Entrepot;
import com.ensah.repository.EntrepotRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntrepotService {
    private final EntrepotRepository repository;

    public EntrepotService(EntrepotRepository repository) {
        this.repository = repository;
    }

    public List<Entrepot> findAll() { return repository.findAll(); }
    public Entrepot save(Entrepot e) { return repository.save(e); }
    public void delete(ObjectId id) { repository.deleteById(id); }
    public void update(Entrepot entrepot) {
        if (entrepot.getId() != null) {
            Optional<Entrepot> existingEntrepot = repository.findById(entrepot.getId());
            
            if (existingEntrepot.isPresent()) {
                Entrepot updatedEntrepot = existingEntrepot.get();
                updatedEntrepot.setNom(entrepot.getNom());
                updatedEntrepot.setCode(entrepot.getCode());
                updatedEntrepot.setAdresse(entrepot.getAdresse());
                repository.save(updatedEntrepot);  
            } else {
                System.out.println("L'entrepôt avec l'ID spécifié n'existe pas.");
            }
        } else {
            System.out.println("L'ID est nul, création d'un nouvel entrepôt.");
            repository.save(entrepot); 
        }
    }


    public Entrepot findByNom(String nom) {
        return repository.findByNom(nom);
    }

    public Entrepot getEntrepotById(ObjectId id) {
        return repository.findById(id).orElse(null);
    }


    public Entrepot findById(ObjectId id) { return repository.findById(id).orElse(null); }
    public List<Entrepot> getAllEntrepots() { return repository.findAll(); }
}
