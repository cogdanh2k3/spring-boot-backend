package com.springboot.scene.controller;
import com.springboot.scene.entity.SceneLevel;
import com.springboot.scene.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/admin/scene")
public class SceneAdminController {
    private final SceneService sceneService;
    @Autowired
    public SceneAdminController(SceneService sceneService) {
        this.sceneService = sceneService;
    }
    @GetMapping
    public String adminPanel(Model model) {
        List<SceneLevel> levels = sceneService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("newLevel", new SceneLevel());
        return "admin/scene/index";
    }
    @PostMapping("/levels")
    public String createLevel(@ModelAttribute SceneLevel newLevel) {
// Assuming locations are set properly in the form
        newLevel.setQuestionCount(newLevel.getLocations().size());
        sceneService.createLevel(newLevel);
        return "redirect:/admin/scene";
    }
    @PostMapping("/levels/{levelId}")
    public String deleteLevel(@PathVariable String levelId) {
        sceneService.deleteLevel(levelId);
        return "redirect:/admin/scene";
    }
}