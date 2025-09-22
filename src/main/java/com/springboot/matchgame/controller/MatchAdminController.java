package com.springboot.matchgame.controller;

import com.springboot.matchgame.entity.MatchLevel;
import com.springboot.matchgame.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/matchgame")
public class MatchAdminController {

    private final MatchService matchService;

    @Autowired
    public MatchAdminController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        List<MatchLevel> levels = matchService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("newLevel", new MatchLevel());
        return "admin/matchgame/index";
    }

    @PostMapping("/levels")
    public String createLevel(@ModelAttribute MatchLevel newLevel) {
        // Assuming pairs are set properly in the form
        newLevel.setPairCount(newLevel.getPairs().size());
        matchService.createLevel(newLevel);
        return "redirect:/admin/matchgame";
    }

    @PostMapping("/levels/{levelId}")
    public String deleteLevel(@PathVariable String levelId) {
        matchService.deleteLevel(levelId);
        return "redirect:/admin/matchgame";
    }
}