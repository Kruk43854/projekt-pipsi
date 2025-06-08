package pl.usterkimiejskie.usterkimiejskie.dto;

import java.time.LocalDateTime;

public class KomentarzDto {
    private Long id;
    private String tresc;
    private String autorUsername;
    private Long usterkaId;
    private LocalDateTime dataDodania;

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTresc() { return tresc; }
    public void setTresc(String tresc) { this.tresc = tresc; }

    public String getAutorUsername() { return autorUsername; }
    public void setAutorUsername(String autorUsername) { this.autorUsername = autorUsername; }

    public Long getUsterkaId() { return usterkaId; }
    public void setUsterkaId(Long usterkaId) { this.usterkaId = usterkaId; }

    public LocalDateTime getDataDodania() {
        return dataDodania;
    }
    public void setDataDodania(LocalDateTime dataDodania){this.dataDodania = dataDodania;}
}