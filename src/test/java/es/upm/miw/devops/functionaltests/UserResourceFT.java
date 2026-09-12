package es.upm.miw.devops.functionaltests;

import es.upm.miw.devops.domain.model.Province;
import es.upm.miw.devops.domain.model.Role;
import es.upm.miw.devops.domain.model.User;
import es.upm.miw.devops.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class UserResourceFT {

    private static final String USERS = "/users";
    private static final UUID EXISTING_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000999");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testReadExistingUser() {
        webTestClient.get()
                .uri(USERS + "/{id}", EXISTING_USER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(EXISTING_USER_ID.toString())
                .jsonPath("$.firstName").isEqualTo("Ana")
                .jsonPath("$.familyName").isEqualTo("Garcia");
    }

    @Test
    void testReadUnknownUser() {
        webTestClient.get()
                .uri(USERS + "/{id}", UNKNOWN_USER_ID)
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND);
    }

    @Test
    void testFindBillableUsers() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USERS).queryParam("billable", "true").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].firstName").isEqualTo("Ana")
                .jsonPath("$[1].firstName").isEqualTo("Luis");
    }

    @Test
    void testFindAllUsersWithoutFilter() {
        webTestClient.get()
                .uri(USERS)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void testDeleteExistingUser() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000200"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.delete@example.com")
                .identity("20000000D")
                .address("Calle Temporal 2")
                .city("Barcelona")
                .province(Province.BARCELONA)
                .postalCode("08001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        webTestClient.delete()
                .uri(USERS + "/{id}", user.getId())
                .exchange()
                .expectStatus().isEqualTo(NO_CONTENT);

        assertThat(this.userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void testDeleteUnknownUser() {
        webTestClient.delete()
                .uri(USERS + "/{id}", UNKNOWN_USER_ID)
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND);
    }
}
