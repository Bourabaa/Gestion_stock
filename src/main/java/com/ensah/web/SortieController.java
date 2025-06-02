package com.ensah.web;

import com.ensah.model.Sortie;
import com.ensah.model.SortieCommande;
import com.ensah.model.SortieDivers;
import com.ensah.service.SortieService;
import com.ensah.service.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/sorties")
public class SortieController {

    @Autowired
    private SortieService sortieService;

    private final EntrepotService entrepotService;
    private final ProduitService produitService;

    public SortieController(SortieService sortieService, EntrepotService entrepotService, ProduitService produitService) {
        this.sortieService = sortieService;
        this.entrepotService = entrepotService;
        this.produitService = produitService;
    }

    @GetMapping
    public String listSorties(Model model) {
        model.addAttribute("sorties", sortieService.findAll());
        model.addAttribute("sortie", new SortieCommande()); // pour le binding par défaut
        model.addAttribute("entrepots", entrepotService.findAll());
        model.addAttribute("produits", produitService.getAllProduits()); // Ajouter produits à la vue
        return "sorties";
    }

    @PostMapping("/save")
    public String saveSortie(
            @RequestParam(required = false) String id,
            @RequestParam String dateSortie,
            @RequestParam ObjectId produit,
            @RequestParam String unite,
            @RequestParam int quantite,
            @RequestParam String type,
            @RequestParam(required = false) String numeroCommande,
            @RequestParam(required = false) String dateCommande,
            @RequestParam(required = false) String client,
            @RequestParam(required = false) String motif,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) ObjectId entrepot,
            Model model
    ) {
        Sortie sortie;

        if ("commande".equals(type)) {
            SortieCommande sc = new SortieCommande();
            sc.setNumeroCommande(numeroCommande);
            sc.setClient(client);
            sc.setDateCommande(LocalDate.parse(dateCommande));
            sortie = sc;
        } else {
            SortieDivers sd = new SortieDivers();
            sd.setMotif(motif);
            sd.setDescription(description);
            sortie = sd;
        }

        if (id != null && !id.isBlank()) sortie.setId(new ObjectId(id));
        sortie.setDateSortie(LocalDate.parse(dateSortie));
        sortie.setProduit(produit);
        sortie.setUnite(unite);
        sortie.setQuantite(quantite);
        sortie.setType(type);
        if (entrepot != null) sortie.setEntrepot(entrepot);

        try {
            sortieService.saveSortie(sortie);
            return "redirect:/sorties";
        } catch (IllegalArgumentException ex) {
            // Afficher le message d'erreur dans la vue
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("sorties", sortieService.findAll());
            model.addAttribute("sortie", sortie);
            model.addAttribute("entrepots", entrepotService.findAll());
            model.addAttribute("produits", produitService.getAllProduits());
            return "sorties";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSortie(@PathVariable ObjectId id) {
        sortieService.delete(id);
        return "redirect:/sorties";
    }
}
