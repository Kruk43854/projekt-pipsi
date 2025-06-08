package pl.usterkimiejskie.usterkimiejskie.service;

import pl.usterkimiejskie.usterkimiejskie.dto.KomentarzDto;
import pl.usterkimiejskie.usterkimiejskie.entity.Komentarz;

public class KomentarzService {
    public KomentarzDto mapToDto(Komentarz komentarz) {
        KomentarzDto dto = new KomentarzDto();
        dto.setId(komentarz.getId());
        dto.setTresc(komentarz.getTresc());
        dto.setAutorUsername(komentarz.getAutor().getUsername());
        dto.setUsterkaId(komentarz.getUsterka().getId());
        return dto;
    }

}
