package com.ensah.repository;

import com.ensah.model.Transfert;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransfertRepository extends MongoRepository<Transfert, ObjectId> {

}
