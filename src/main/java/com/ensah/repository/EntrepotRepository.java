package com.ensah.repository;

import com.ensah.model.Entrepot;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntrepotRepository extends MongoRepository<Entrepot, ObjectId> {
    // Vous pouvez ajouter d'autres méthodes spécifiques si nécessaire

    Entrepot findByNom(String entrepotNom);


}

