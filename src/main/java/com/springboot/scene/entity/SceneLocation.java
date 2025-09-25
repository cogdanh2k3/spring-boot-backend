// 2. SceneLocation.java
package com.springboot.scene.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "scene_locations")
public class SceneLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String locationId;

    @NotBlank
    private String locationName;

    @NotBlank
    private String imageUrl;

    @NotNull
    @Column(name = "true_lat")
    private Double trueLat;

    @NotNull
    @Column(name = "true_lon")
    private Double trueLon;

    private String description;

    private String country;

    private String city;

    // Constructors
    public SceneLocation() {}

    public SceneLocation(String locationId, String locationName, String imageUrl,
                         Double trueLat, Double trueLon, String description,
                         String country, String city) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.imageUrl = imageUrl;
        this.trueLat = trueLat;
        this.trueLon = trueLon;
        this.description = description;
        this.country = country;
        this.city = city;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocationId() { return locationId; }
    public void setLocationId(String locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getTrueLat() { return trueLat; }
    public void setTrueLat(Double trueLat) { this.trueLat = trueLat; }

    public Double getTrueLon() { return trueLon; }
    public void setTrueLon(Double trueLon) { this.trueLon = trueLon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}