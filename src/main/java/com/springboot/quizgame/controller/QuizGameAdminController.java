package com.springboot.quizgame.controller;

import com.springboot.quizgame.entity.QuizGameLevel;
import com.springboot.quizgame.service.QuizGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/quizgame")
public class QuizGameAdminController {

    private final QuizGameService quizGameService;

    @Autowired
    public QuizGameAdminController(QuizGameService quizGameService) {
        this.quizGameService = quizGameService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        List<QuizGameLevel> levels = quizGameService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("newLevel", new QuizGameLevel());
        return "admin/quizgame/index";
    }

    @PostMapping("/levels")
    public String createLevel(@ModelAttribute QuizGameLevel newLevel) {
        // Assuming questions are set properly in the form
        newLevel.setQuestionCount(newLevel.getQuestions().size());
        quizGameService.createLevel(newLevel);
        return "redirect:/admin/quizgame";
    }

    @PostMapping("/levels/{levelId}")
    public String deleteLevel(@PathVariable String levelId) {
        quizGameService.deleteLevel(levelId);
        return "redirect:/admin/quizgame";
    }
}