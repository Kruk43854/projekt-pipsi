package pl.usterkimiejskie.usterkimiejskie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsterkaDto {

    private Long id;

    @NotBlank(message = "Tytuł usterki nie może być pusty")
    private String tytul;

    private String status;
    private String adres;
    private double lat;
    private double lng;

    private Long userId;

    // Gettery i settery

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTytul() { return tytul; }
    public void setTytul(String tytul) { this.tytul = tytul; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
