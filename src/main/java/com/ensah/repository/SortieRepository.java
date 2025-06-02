package com.ensah.repository;

import com.ensah.model.Sortie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SortieRepository extends MongoRepository<Sortie, ObjectId> {
}
