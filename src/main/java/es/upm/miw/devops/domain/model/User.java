package es.upm.miw.devops.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "miwUser")
public class User {
    @Id
    private UUID id;
    private String firstName;
    private String familyName;
    @Column(unique = true, nullable = false)
    private String email;
    private String identity;
    private String address;
    private String city;
    @Enumerated(EnumType.STRING)
    private Province province;
    private String postalCode;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean active;

    public boolean isBillable() {
        return this.hasContent(this.firstName)
                && this.hasContent(this.familyName)
                && this.hasContent(this.email)
                && this.hasContent(this.identity)
                && this.hasContent(this.address)
                && this.hasContent(this.city)
                && this.province != null
                && this.hasContent(this.postalCode);
    }

    private boolean hasContent(String value) {
        return value != null && !value.isBlank();
    }
}
