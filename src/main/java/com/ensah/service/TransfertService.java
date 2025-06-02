package com.ensah.service;

import com.ensah.model.Transfert;
import com.ensah.repository.TransfertRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TransfertService {
    private final TransfertRepository transfertRepository;
    public TransfertService(TransfertRepository transfertRepository) {
        this.transfertRepository = transfertRepository;
    }
    public List<Transfert> findAll() {return transfertRepository.findAll();}
    public Transfert findById(ObjectId id) {return transfertRepository.findById(id).orElse(null);}
//    public Transfert save(Transfert transfert) {return transfertRepository.save(transfert);}
    public void delete(Transfert transfert) {transfertRepository.delete(transfert);}
    public void save(Transfert transfert) {
        transfertRepository.save(transfert);
    }



}
