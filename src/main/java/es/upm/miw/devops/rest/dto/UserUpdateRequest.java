package es.upm.miw.devops.rest.dto;

import es.upm.miw.devops.domain.model.Province;
import es.upm.miw.devops.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String familyName;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String identity;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotNull
    private Province province;
    @NotBlank
    private String postalCode;
    @NotNull
    private Role role;
    private Boolean active;
}
