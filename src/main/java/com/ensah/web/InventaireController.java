package com.ensah.web;

import com.ensah.model.Entrepot;
import com.ensah.model.Inventaire;
import com.ensah.model.LigneInventaire;
import com.ensah.model.Produit;
import com.ensah.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/inventaires")
public class InventaireController {

    @Autowired private InventaireService inventaireService;
    @Autowired private StockService stockService;
    @Autowired private EntrepotService entrepotService;
    @Autowired private ProduitService produitService;

    @GetMapping
    public String listerInventaires(@RequestParam(required = false) String date,
                                    @RequestParam(required = false) ObjectId entrepotId,
                                    Model model) {
        model.addAttribute("entrepots", entrepotService.findAll());
        List<Inventaire> inventaires = inventaireService.getAllInventaires();

        if (date != null && !date.isEmpty()) {
            try {
                LocalDate d = LocalDate.parse(date);
                inventaires = inventaires.stream()
                        .filter(i -> i.getDateInventaire() != null && i.getDateInventaire().equals(d))
                        .toList();
                model.addAttribute("date", date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (entrepotId != null) {
            inventaires = inventaires.stream()
                    .filter(i -> i.getEntrepotId() != null && i.getEntrepotId().equals(entrepotId))
                    .toList();
            model.addAttribute("entrepot", entrepotId);
        }

        model.addAttribute("inventaires", inventaires);
        return "inventaires";
    }

    @GetMapping("/nouveau")
    public String nouveauInventaireForm(Model model) {
        model.addAttribute("entrepots", entrepotService.findAll());
        return "nouveau_inventaire";
    }

    @PostMapping("/generate-template")
    public ResponseEntity<InputStreamResource> generateExcelTemplate(
            @RequestParam ObjectId entrepotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInventaire) throws IOException {

        ByteArrayInputStream in = generateExcel(entrepotId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=inventaire_" + entrepotId + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }

//    @PostMapping("/upload")
//    public String uploadExcel(@RequestParam("file") MultipartFile file,
//                              @RequestParam ObjectId entrepotId,
//                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInventaire,
//                              Model model) {
//        try {
//            List<LigneInventaire> lignes = parseExcel(file.getInputStream());
//
//            Inventaire inventaire = new Inventaire();
//            inventaire.setLignes(lignes);
//            inventaire.setEntrepotId(entrepotId);
//            inventaire.setDateInventaire(dateInventaire);
//
//            Entrepot entrepotObj = entrepotService.findById(entrepotId);
//            model.addAttribute("entrepotNom", entrepotObj != null ? entrepotObj.getNom() : "Inconnu");
//
//            Map<String, List<String>> produitUnites = new HashMap<>();
//            for (LigneInventaire ligne : lignes) {
//                Produit p = produitService.getProduitParNom(ligne.getProduit());
//                if (p != null && p.getUnitesDisponibles() != null) {
//                    produitUnites.put(ligne.getProduit(), p.getUnitesDisponibles());
//                }
//            }
//
//            model.addAttribute("inventaire", inventaire);
//            model.addAttribute("valide", true);
//            model.addAttribute("produitUnites", produitUnites);
//
//        } catch (Exception e) {
//            model.addAttribute("message", "Erreur de lecture du fichier.");
//        }
//
//        return "resultats_inventaire";
//    }
@PostMapping("/upload")
public String uploadExcel(@RequestParam("file") MultipartFile file,
                          @RequestParam String entrepotId, // Change to String here
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInventaire,
                          Model model) {
    try {
        // Convert String to ObjectId
        ObjectId entrepotObjectId = new ObjectId(entrepotId);

        List<LigneInventaire> lignes = parseExcel(file.getInputStream());

        Inventaire inventaire = new Inventaire();
        inventaire.setLignes(lignes);
        inventaire.setEntrepotId(entrepotObjectId);
        inventaire.setDateInventaire(dateInventaire);

        Entrepot entrepotObj = entrepotService.findById(entrepotObjectId);
        model.addAttribute("entrepotNom", entrepotObj != null ? entrepotObj.getNom() : "Inconnu");

        Map<String, List<String>> produitUnites = new HashMap<>();
        for (LigneInventaire ligne : lignes) {
            Produit p = produitService.getProduitParNom(ligne.getProduit());
            if (p != null && p.getUnitesDisponibles() != null) {
                produitUnites.put(ligne.getProduit(), p.getUnitesDisponibles());
            }
        }

        model.addAttribute("inventaire", inventaire);
        model.addAttribute("valide", true);
        model.addAttribute("produitUnites", produitUnites);

    } catch (Exception e) {
        model.addAttribute("message", "Erreur de lecture du fichier.");
    }

    return "resultats_inventaire";
}

    @PostMapping("/valider")
    public String validerInventaire(@RequestParam ObjectId entrepotId,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInventaire,
                                    @RequestParam List<String> produit,
                                    @RequestParam List<String> unite,
                                    @RequestParam List<Double> theorique,
                                    @RequestParam List<Double> reel,
                                    @RequestParam(required = false) String remarque,
                                    @RequestParam(required = false) List<String> justification,
                                    Model model) {

        List<LigneInventaire> lignes = new ArrayList<>();

        for (int i = 0; i < produit.size(); i++) {
            LigneInventaire ligne = new LigneInventaire();
            ligne.setProduit(produit.get(i));
            ligne.setUnite(unite.get(i));
            ligne.setStockThéorique(theorique.get(i));

            Produit p = produitService.getProduitParNom(produit.get(i));
            double quantiteBase = reel.get(i);

            if (p != null) {
                quantiteBase = unite.get(i).equals(p.getUniteDeBase())
                        ? reel.get(i)
                        : p.convertirEnUniteDeBase(unite.get(i), reel.get(i));

                stockService.ajusterStock(p.getId(), entrepotId, quantiteBase, p.getUniteDeBase());
            }

            ligne.setStockRéel(quantiteBase);

            if (justification != null && i < justification.size()) {
                ligne.setJustification(justification.get(i));
            }

            lignes.add(ligne);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Inventaire inventaire = new Inventaire();
        inventaire.setDateInventaire(dateInventaire);
        inventaire.setEntrepotId(entrepotId);
        inventaire.setLignes(lignes);
        inventaire.setValide(true);
        inventaire.setEffectuePar(username);
        inventaire.setValidePar(username);
        inventaire.setRemarque(remarque);

        Entrepot entrepotObj = entrepotService.findById(entrepotId);
        model.addAttribute("entrepotNom", entrepotObj != null ? entrepotObj.getNom() : "Inconnu");
        inventaireService.saveInventaire(inventaire);

        return "redirect:/inventaires";
    }

    private ByteArrayInputStream generateExcel(ObjectId entrepotId) {
        Entrepot entrepot = entrepotService.findById(entrepotId);
        if (entrepot == null) throw new RuntimeException("Entrepôt introuvable");

        List<Produit> produits = stockService.getProduitsParEntrepot(entrepotId.toString());

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventaire");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Produit");
            header.createCell(1).setCellValue("Unité");
            header.createCell(2).setCellValue("Stock théorique");
            header.createCell(3).setCellValue("Stock réel");

            int rowNum = 1;
            for (Produit produit : produits) {
                Row row = sheet.createRow(rowNum++);
                double stockTheo = stockService.getQuantiteProduitDansEntrepot(
                        produit.getId().toString(),
                        entrepotId.toString()
                );

                row.createCell(0).setCellValue(produit.getNom());
                row.createCell(1).setCellValue(produit.getUniteDeBase());
                row.createCell(2).setCellValue(stockTheo);
                row.createCell(3).setCellValue("");
            }

            for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Erreur génération Excel", e);
        }
    }

    private List<LigneInventaire> parseExcel(InputStream input) throws IOException {
        List<LigneInventaire> lignes = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(input);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null) {
                LigneInventaire ligne = new LigneInventaire();
                ligne.setProduit(getCellStringValue(row.getCell(0)));
                ligne.setUnite(getCellStringValue(row.getCell(1)));
                Double stockTheo = getCellNumericValue(row.getCell(2));
                Double stockReel = getCellNumericValue(row.getCell(3));
                ligne.setStockThéorique(stockTheo != null ? stockTheo : 0.0);
                ligne.setStockRéel(stockReel != null ? stockReel : 0.0);
                lignes.add(ligne);
            }
        }

        workbook.close();
        return lignes;
    }

    private String getCellStringValue(Cell cell) {
        return (cell != null) ? cell.toString().trim() : "";
    }

    private Double getCellNumericValue(Cell cell) {
        if (cell == null) return 0.0;
        try {
            if (cell.getCellType() == CellType.ERROR) {
                return 0.0;
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                String value = cell.toString().replace(",", ".").trim();
                if (value.isBlank()) return 0.0;
                return Double.parseDouble(value);
            }
        } catch (Exception e) {
            System.err.println("Erreur de lecture cellule [" + cell + "] : " + e.getMessage());
            return 0.0;
        }
    }
}