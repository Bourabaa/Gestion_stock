package com.ensah.web;

import com.ensah.model.Reception;
import com.ensah.service.*;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/receptions2")
public class NewReceptionController {

    @Autowired
    private ReceptionService receptionService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private EntrepotService entrepotService;

    @Autowired
    private StockService stockService;

    @GetMapping
    public String redirectToNouvelleReception() {
        return "redirect:/receptions2/nouvelle";
    }

    @GetMapping("/nouvelle")
    public String nouvelleReception(Model model) {
        model.addAttribute("receptions2", receptionService.getAllReceptions());
        model.addAttribute("reception", new Reception());
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("entrepots", entrepotService.findAll());
        return "nouvelle_reception";
    }

//    @PostMapping("/save")
//    public String saveReception(@RequestParam("remarque") String remarque,
//                                @RequestParam("dateReception") LocalDate dateReception,
//                                @RequestParam("numAchat") String numAchat,
//                                @RequestParam("produitId") String produitIdStr,
//                                @RequestParam("unite") String unite,
//                                @RequestParam("quantite") double quantite,
//                                @RequestParam("entrepotId") String entrepotIdStr,
//                                @RequestParam("source") String sourceStr) {
//
//        ObjectId produitId = new ObjectId(produitIdStr);
//        ObjectId entrepotId = new ObjectId(entrepotIdStr);
//
//        Reception reception = new Reception();
//        reception.setNumAchat(numAchat);
//        reception.setDateReception(dateReception);
//        reception.setProduitId(produitId);
//        reception.setEntrepotId(entrepotId);
//        reception.setUnite(unite);
//        reception.setRemarque(remarque);
//        reception.setQuantite(quantite);
//        reception.setSource(sourceStr);
//
//        stockService.enregistrerReception(reception);  // ✅ utiliser la logique centralisée
//        receptionService.saveReception(reception);
//
//        return "redirect:/receptions2/nouvelle";
//    }
@PostMapping("/save")
public String saveReception(@Valid @ModelAttribute("reception") Reception reception,
                            BindingResult result,
                            Model model) {

    if (result.hasErrors()) {
        // Renvoie à la page du formulaire avec les erreurs
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("entrepots", entrepotService.getAllEntrepots());
        return "nouvelle_reception"; // remplace par le bon nom de ton template
    }

  //  stockService.enregistrerReception(reception);
    receptionService.saveReception(reception);

    return "redirect:/receptions2/nouvelle";
}


}