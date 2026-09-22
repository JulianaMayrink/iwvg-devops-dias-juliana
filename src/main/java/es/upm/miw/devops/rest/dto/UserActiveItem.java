package es.upm.miw.devops.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActiveItem {
    @NotNull
    private UUID id;
    @NotNull
    private Boolean active;
}
