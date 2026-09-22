package es.upm.miw.devops.rest.dto;

import es.upm.miw.devops.domain.model.Province;
import es.upm.miw.devops.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String firstName;
    private String familyName;
    private String email;
    private String identity;
    private String address;
    private String city;
    private Province province;
    private String postalCode;
    private Role role;
}
