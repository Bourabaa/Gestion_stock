package com.ensah.repository;

import com.ensah.model.Stock;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockRepository extends MongoRepository<Stock, ObjectId> {

    List<Stock> findByEntrepotId(ObjectId entrepotId);

    Stock findByProduitIdAndEntrepotId(ObjectId produitId, ObjectId entrepotId);



}
