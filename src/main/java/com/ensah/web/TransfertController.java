package com.ensah.web;

import com.ensah.model.Entrepot;
import com.ensah.model.Produit;
import com.ensah.model.Transfert;
import com.ensah.service.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/transferts")
public class TransfertController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private EntrepotService entrepotService;

    @Autowired
    private TransfertService transfertService;


    @GetMapping
    public String listTransferts(Model model) {
        List<Transfert> transferts = transfertService.findAll();

        for (Transfert t : transferts) {
            if (t.getProduitId() != null) {
                Produit p = produitService.getProduitById(t.getProduitId());
                t.setProduit(p);
            }

            if (t.getEntrepotSource() != null) {
                Entrepot eSource = entrepotService.findById(t.getEntrepotSource());
                if (eSource != null) {
                    t.setEntrepotSourceNom(eSource.getNom());
                }
            }

            if (t.getEntrepotDestiny() != null) {
                Entrepot eDest = entrepotService.findById(t.getEntrepotDestiny());
                if (eDest != null) {
                    t.setEntrepotDestinyNom(eDest.getNom());
                }
            }
        }

        model.addAttribute("transferts", transferts);
        return "transferts";
    }
    @GetMapping("/nouveau")
    public String nouveauTransfertForm(Model model) {
        model.addAttribute("transfert", new Transfert());
        model.addAttribute("entrepots", entrepotService.findAll());
        model.addAttribute("produits", produitService.getAllProduits());
        return "nouveau-transfert";
    }

//    @PostMapping("/save")
//    public String saveTransfert(@ModelAttribute("transfert") Transfert transfert,
//                                RedirectAttributes redirectAttributes) {
//        try {
//            stockService.transfererProduit(transfert);
//            transfertService.save(transfert);
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("messageErreur", e.getMessage());
//            return "redirect:/transferts/nouveau";
//        }
//        return "redirect:/transferts";
//    }


    @PostMapping("/save")
    public String saveTransfert(@ModelAttribute("transfert") Transfert transfert,
                                RedirectAttributes redirectAttributes) {
        try {
            stockService.transfererProduit(transfert);
            transfertService.save(transfert);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors du transfert : " + e.getMessage());  // <--- Ajoute ce log
            redirectAttributes.addFlashAttribute("messageErreur", e.getMessage());
            return "redirect:/transferts/nouveau";
        }
        return "redirect:/transferts";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransfert(@PathVariable ObjectId id) {
        Transfert transfert = transfertService.findById(id);
        if (transfert != null) {
            transfertService.delete(transfert);
        }
        return "redirect:/transferts";
    }
}