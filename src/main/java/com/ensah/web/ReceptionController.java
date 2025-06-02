package com.ensah.web;

import com.ensah.model.Entrepot;
import com.ensah.model.Produit;
import com.ensah.model.Reception;
import com.ensah.service.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/receptions")
public class ReceptionController {

    @Autowired
    private ReceptionService receptionService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private EntrepotService entrepotService;

    @Autowired
    private StockService stockService;

    // Affiche toutes les réceptions
    @GetMapping
    public String afficherReceptions(Model model) {
        model.addAttribute("receptions", receptionService.getAllReceptions());
        model.addAttribute("reception_a", new Reception());
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("entrepots", entrepotService.findAll());
        return "receptions";
    }

    // Formulaire pour une nouvelle réception
    @GetMapping("/nouvelle")
    public String nouvelleReception(Model model) {
        model.addAttribute("reception", new Reception());
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("entrepots", entrepotService.findAll());
        return "nouvelle_reception";
    }

    // Sauvegarde une nouvelle réception
    @PostMapping("/save")
    public String saveReception(@RequestParam("dateReception") LocalDate dateReception,
                                @RequestParam("numAchat") String numAchat,
                                @RequestParam("produitId") ObjectId produitId,
                                @RequestParam("unite") String unite,
                                @RequestParam("quantite") double quantite,
                                @RequestParam("entrepotId") ObjectId entrepotId,
                                @RequestParam("source") String sourceStr) {

        // Validation basique sur les champs
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro.");
        }

        if (dateReception == null) {
            throw new IllegalArgumentException("La date d'achat est obligatoire.");
        }

        if (numAchat == null || numAchat.isEmpty()) {
            throw new IllegalArgumentException("Le numéro d'achat est obligatoire.");
        }

        // Création d'une nouvelle réception
        Reception reception = new Reception();
        reception.setNumAchat(numAchat);
        reception.setUnite(unite);
        reception.setDateReception(dateReception);
        reception.setQuantite(quantite);
        reception.setProduitId(produitId);
        reception.setEntrepotId(entrepotId);
        reception.setSource(sourceStr);

        // Appel des services pour enregistrer la réception
        stockService.enregistrerReception(reception);
        receptionService.saveReception(reception);

        return "redirect:/receptions";
    }

    // Filtrage optimisé des réceptions
    @GetMapping("/filtrer")
    public String filtrer(@RequestParam(required = false) String dateDebut,
                          @RequestParam(required = false) String dateFin,
                          @RequestParam(required = false) String produitId,
                          @RequestParam(required = false) String entrepotId,
                          Model model) {

        // Conversion de produitId et entrepotId en ObjectId si fourni
        ObjectId produitObjectId = null;
        ObjectId entrepotObjectId = null;

        if (produitId != null && !produitId.isEmpty()) {
            try {
                produitObjectId = new ObjectId(produitId);
            } catch (IllegalArgumentException e) {
                System.out.println("ID produit invalide : " + produitId);
            }
        }

        if (entrepotId != null && !entrepotId.isEmpty()) {
            try {
                entrepotObjectId = new ObjectId(entrepotId);
            } catch (IllegalArgumentException e) {
                System.out.println("ID entrepôt invalide : " + entrepotId);
            }
        }

        // Récupération des réceptions filtrées
        List<Reception> receptions = receptionService.filtrerReceptions(dateDebut, dateFin, produitObjectId, entrepotObjectId);

        // Ajouter les objets enrichis pour chaque réception (Produit et Entrepôt)
        for (Reception r : receptions) {
            if (r.getProduitId() != null) {
                Produit p = produitService.getProduitById(r.getProduitId());
                r.setProduit(p);
            }
            if (r.getEntrepotId() != null) {
                Entrepot e = entrepotService.getEntrepotById(r.getEntrepotId());
                r.setEntrepot(e);
            }
        }

        // Remplir le modèle pour la vue Thymeleaf
        model.addAttribute("receptions", receptions);
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("entrepots", entrepotService.findAll());

        // Conserver les valeurs des filtres pour les afficher dans les champs
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        model.addAttribute("produitId", produitId);
        model.addAttribute("entrepotId", entrepotId);

        return "receptions";
    }




    // Suppression d'une réception par ID
    @GetMapping("/delete/{id}")
    public String supprimerReception(@PathVariable ObjectId id) {
        receptionService.deleteReception(id);
        return "redirect:/receptions";
    }
}