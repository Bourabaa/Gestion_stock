package com.ensah.web;

import com.ensah.model.Produit;
import com.ensah.model.Entrepot;
import com.ensah.model.Stock;
import com.ensah.service.ProduitService;
import com.ensah.service.EntrepotService;
import com.ensah.service.StockService;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private EntrepotService entrepotService;

    // Liste des stocks
    @GetMapping("/liste")
    public String listStocks(Model model) {
        List<Stock> stocks = stockService.findAll();

        for (Stock stock : stocks) {
            Produit produit = produitService.getProduitById(stock.getProduitId());
            Entrepot entrepot = entrepotService.findById(stock.getEntrepotId());

            if (produit == null || entrepot == null) {
                throw new IllegalArgumentException("Produit ou Entrepôt introuvable pour un stock.");
            }

            stock.setProduit(produit);
            stock.setEntrepot(entrepot);
        }

        model.addAttribute("stocks", stocks);
        return "stocks"; // fichier Thymeleaf
    }

    // Supprimer un stock (si vide)
    @GetMapping("/delete/{id}")
    public String deleteStock(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            stockService.deleteById(new ObjectId(id));
            redirectAttributes.addFlashAttribute("message", "Stock supprimé avec succès.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur inattendue lors de la suppression.");
        }

        return "redirect:/stocks/liste";
    }



}