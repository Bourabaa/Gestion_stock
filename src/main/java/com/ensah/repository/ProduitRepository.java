package com.ensah.repository;

import com.ensah.model.Produit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProduitRepository extends MongoRepository<Produit, ObjectId> {
    // Vous pouvez ajouter des méthodes personnalisées si nécessaire
    Produit findByNom(String nom);
}
