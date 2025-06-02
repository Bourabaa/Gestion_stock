package com.ensah.repository;

import com.ensah.model.Inventaire;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InventaireRepository extends MongoRepository<Inventaire, ObjectId> {

}