package com.springboot.soundgame.controller;

import com.springboot.soundgame.entity.SoundLevel;
import com.springboot.soundgame.service.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/sounds")
public class SoundAdminController {
    private final SoundService soundService;

    @Autowired
    public SoundAdminController(SoundService soundService) {
        this.soundService = soundService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        List<SoundLevel> levels = soundService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("newLevel", new SoundLevel());
        return "admin/sounds/index";
    }

    @PostMapping("levels")
    public String createLevel(@ModelAttribute SoundLevel newLevel) {
        newLevel.setQuestionCount(newLevel.getClips().size());
        soundService.createLevel(newLevel);
        return "redirect:/admin/sound";
    }

    @PostMapping("levels/{levelId}")
    public String deleteLevel(@PathVariable String levelId) {
        soundService.deleteLevel(levelId);
        return "redirect:/admin/sound";
    }
}

