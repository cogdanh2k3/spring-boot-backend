package com.springboot.matchgame.controller;

import com.springboot.matchgame.entity.MatchGameLevel;
import com.springboot.matchgame.service.MatchGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/matchgame")
public class MatchGameAdminController {

    private final MatchGameService matchGameService;

    @Autowired
    public MatchGameAdminController(MatchGameService matchGameService) {
        this.matchGameService = matchGameService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        List<MatchGameLevel> levels = matchGameService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("newLevel", new MatchGameLevel());
        return "admin/matchgame/index";
    }

    @PostMapping("/levels")
    public String createLevel(@ModelAttribute MatchGameLevel newLevel) {
        // Assuming pairs are set properly in the form
        newLevel.setPairCount(newLevel.getPairs().size());
        matchGameService.createLevel(newLevel);
        return "redirect:/admin/matchgame";
    }

    @PostMapping("/levels/{levelId}")
    public String deleteLevel(@PathVariable String levelId) {
        matchGameService.deleteLevel(levelId);
        return "redirect:/admin/matchgame";
    }
}