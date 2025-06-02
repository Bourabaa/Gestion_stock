package com.ensah.repository;

import com.ensah.model.Reception;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceptionRepository extends MongoRepository<Reception, ObjectId> {

}
