package com.springboot.controller;

import com.springboot.entity.WordSearchTopic;
import com.springboot.service.WordSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final WordSearchService wordSearchService;

    @Autowired
    public AdminController(WordSearchService wordSearchService) {
        this.wordSearchService = wordSearchService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        List<WordSearchTopic> topics = wordSearchService.getAllTopics();
        model.addAttribute("topics", topics);
        model.addAttribute("newTopic", new WordSearchTopic());
        return "admin/index";
    }

    @PostMapping("/topics")
    public String createTopic(@ModelAttribute WordSearchTopic newTopic) {
        String[] wordsArray = newTopic.getWords().toString()
                .replace("[", "")
                .replace("]", "")
                .split(",\\s*");
        List<String> words = Arrays.asList(wordsArray);
        newTopic.setWords(words);
        newTopic.setWordCount(words.size());
        wordSearchService.createTopic(newTopic);
        return "redirect:/admin";
    }

    @PostMapping("/topics/{topicId}")
    public String deleteTopic(@PathVariable String topicId) {
        wordSearchService.deleteTopic(topicId);
        return "redirect:/admin";
    }
}