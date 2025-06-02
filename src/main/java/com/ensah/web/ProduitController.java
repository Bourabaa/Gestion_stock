package com.ensah.web;

import com.ensah.model.Produit;
import com.ensah.service.ProduitService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;


@Controller
@RequestMapping("/produits")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    @GetMapping
    public String afficherProduits(Model model) {
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("produit", new Produit());
        return "produits";
    }

    @PostMapping("/save")
    public String enregistrerProduit(@Valid @ModelAttribute Produit produit, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("produits", produitService.getAllProduits());
            return "produits"; // On reste sur la page pour afficher les erreurs
        }
        produitService.saveProduit(produit);
        return "redirect:/produits";
    }

    @GetMapping("/delete/{id}")
    public String supprimerProduit(@PathVariable("id") ObjectId id) {
        produitService.deleteProduit(id);
        return "redirect:/produits";
    }

    @GetMapping("/edit/{id}")
    public String modifierProduit(@PathVariable("id") ObjectId id, Model model) {
        Produit produit = produitService.getProduitById(id);
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("produit", produit);
        return "produits";
    }

    @GetMapping("/unites/{id}")
    @ResponseBody
    public List<String> getUnitesProduit(@PathVariable("id") ObjectId id) {
        Produit produit = produitService.getProduitById(id);
        if (produit == null) return List.of();

        List<String> unites = new ArrayList<>();

        // Ajouter l'unité de base en premier
        unites.add(produit.getUniteDeBase());

        // Ajouter les autres unités disponibles (sauf doublon)
        if (produit.getUnitesDisponibles() != null) {
            for (String unite : produit.getUnitesDisponibles()) {
                if (!unites.contains(unite)) {
                    unites.add(unite);
                }
            }
        }

        return unites;
    }
}