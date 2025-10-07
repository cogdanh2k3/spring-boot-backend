package com.springboot.soundgame.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "sound_clips")
public class SoundClip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String clipId;

    @NotBlank
    private String name;

    @NotBlank
    private String audioUrl;

    @NotBlank
    private String answer;

    @ManyToOne
    @JoinColumn(name = "level_id")
    @JsonIgnore  // ← THÊM DÒNG NÀY
    private SoundLevel level;

    public SoundClip() {}

    public SoundClip(String clipId, String name, String audioUrl, String answer) {
        this.clipId = clipId;
        this.name = name;
        this.audioUrl = audioUrl;
        this.answer = answer;
    }

    // Getters / Setters (giữ nguyên)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClipId() { return clipId; }
    public void setClipId(String clipId) { this.clipId = clipId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public SoundLevel getLevel() { return level; }
    public void setLevel(SoundLevel level) { this.level = level; }
}