package es.upm.miw.devops.domain;

import es.upm.miw.devops.domain.model.Province;
import es.upm.miw.devops.domain.model.Role;
import es.upm.miw.devops.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User fullUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .firstName("Ana")
                .familyName("Garcia")
                .email("ana.garcia@example.com")
                .identity("12345678A")
                .address("Calle Mayor 1")
                .city("Madrid")
                .province(Province.MADRID)
                .postalCode("28001")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }

    @Test
    void testIsBillableWhenAllFieldsHaveContent() {
        assertThat(this.fullUser().isBillable()).isTrue();
    }

    @Test
    void testIsNotBillableWhenAFieldIsNull() {
        User user = this.fullUser();
        user.setEmail(null);
        assertThat(user.isBillable()).isFalse();
    }

    @Test
    void testIsNotBillableWhenAFieldIsBlank() {
        User user = this.fullUser();
        user.setCity("   ");
        assertThat(user.isBillable()).isFalse();
    }

    @Test
    void testIsNotBillableWhenProvinceIsNull() {
        User user = this.fullUser();
        user.setProvince(null);
        assertThat(user.isBillable()).isFalse();
    }
}
