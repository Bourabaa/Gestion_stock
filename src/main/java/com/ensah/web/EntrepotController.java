package com.ensah.web;

import com.ensah.model.Entrepot;
import com.ensah.service.EntrepotService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


@Controller
@RequestMapping("/entrepots")
public class EntrepotController {
    private final EntrepotService service;

    public EntrepotController(EntrepotService service) {
        this.service = service;
    }

    @GetMapping
    public String listEntrepots(Model model) {
        model.addAttribute("entrepots", service.findAll());
        model.addAttribute("entrepot", new Entrepot());
        return "entrepots";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Entrepot entrepot, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("entrepots", service.findAll());  // Recharger la liste pour la page
            return "entrepots";  // Revenir à la page avec les erreurs affichées
        }

        if (entrepot.getId() == null) {
            service.save(entrepot);
        } else {
            service.update(entrepot);
        }
        return "redirect:/entrepots";
    }



    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            service.delete(objectId);
        } catch (IllegalArgumentException e) {
            // You can redirect to an error page or log this
            System.out.println("Invalid ObjectId: " + id);
            return "redirect:/error";
        }
        return "redirect:/entrepots";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable ObjectId id, Model model) {  
        model.addAttribute("entrepot", service.findById(id));
        model.addAttribute("entrepots", service.findAll());
        return "entrepots";
    }
}
