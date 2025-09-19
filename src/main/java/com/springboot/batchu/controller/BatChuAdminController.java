package com.springboot.batchu.controller;

import com.springboot.batchu.entity.batChuLevel;
import com.springboot.batchu.service.BatChuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/batchu")
public class BatChuAdminController {

    private final BatChuService batChuService;

    @Autowired
    public BatChuAdminController(BatChuService batChuService) {
        this.batChuService = batChuService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        List<batChuLevel> levels = batChuService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("newLevel", new batChuLevel());
        return "admin/batchu/index";
    }

    @PostMapping("/levels")
    public String createLevel(@ModelAttribute batChuLevel newLevel) {
        // Assuming questions are set properly in the form
        newLevel.setQuestionCount(newLevel.getQuestions().size());
        batChuService.createLevel(newLevel);
        return "redirect:/admin/batchu";
    }

    @PostMapping("/levels/{levelId}")
    public String deleteLevel(@PathVariable String levelId) {
        batChuService.deleteLevel(levelId);
        return "redirect:/admin/batchu";
    }
}