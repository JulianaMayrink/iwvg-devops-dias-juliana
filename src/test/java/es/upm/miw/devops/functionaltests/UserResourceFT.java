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

    @Test
    void testUpdateActiveExistingUser() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000201"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.put@example.com")
                .identity("20000001F")
                .address("Calle Temporal 4")
                .city("Barcelona")
                .province(Province.BARCELONA)
                .postalCode("08001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        webTestClient.put()
                .uri(USERS + "/{id}/active", user.getId())
                .bodyValue("{\"active\":false}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(false);

        assertThat(this.userRepository.findById(user.getId()).orElseThrow().getActive()).isFalse();

        this.userRepository.deleteById(user.getId());
    }

    @Test
    void testUpdateActiveUnknownUser() {
        webTestClient.put()
                .uri(USERS + "/{id}/active", UNKNOWN_USER_ID)
                .bodyValue("{\"active\":false}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND);
    }

    @Test
    void testUpdateExistingUser() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000202"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.update@example.com")
                .identity("20000002I")
                .address("Calle Temporal 6")
                .city("Madrid")
                .province(Province.MADRID)
                .postalCode("28001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        String body = """
                {
                  "firstName": "Updated",
                  "familyName": "Name",
                  "email": "updated@example.com",
                  "identity": "20000003J",
                  "address": "Calle Nueva 2",
                  "city": "Barcelona",
                  "province": "BARCELONA",
                  "postalCode": "08001",
                  "role": "ADMIN"
                }
                """;

        webTestClient.put()
                .uri(USERS + "/{id}", user.getId())
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Updated")
                .jsonPath("$.familyName").isEqualTo("Name")
                .jsonPath("$.province").isEqualTo("BARCELONA")
                .jsonPath("$.role").isEqualTo("ADMIN");

        assertThat(this.userRepository.findById(user.getId()).orElseThrow().getFirstName()).isEqualTo("Updated");

        this.userRepository.deleteById(user.getId());
    }

    @Test
    void testUpdateUnknownUser() {
        String body = """
                {
                  "firstName": "Updated",
                  "familyName": "Name",
                  "email": "updated@example.com",
                  "identity": "20000003J",
                  "address": "Calle Nueva 2",
                  "city": "Barcelona",
                  "province": "BARCELONA",
                  "postalCode": "08001",
                  "role": "ADMIN"
                }
                """;

        webTestClient.put()
                .uri(USERS + "/{id}", UNKNOWN_USER_ID)
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isEqualTo(NOT_FOUND);
    }
}
