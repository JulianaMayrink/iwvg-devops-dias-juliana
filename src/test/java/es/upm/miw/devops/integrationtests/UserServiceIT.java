package es.upm.miw.devops.integrationtests;

import es.upm.miw.devops.domain.model.Province;
import es.upm.miw.devops.domain.model.Role;
import es.upm.miw.devops.domain.model.User;
import es.upm.miw.devops.persistence.UserRepository;
import es.upm.miw.devops.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIT {

    private static final UUID EXISTING_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UNKNOWN_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000999");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testReadExistingUser() {
        User user = this.userService.read(EXISTING_USER_ID);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(EXISTING_USER_ID);
        assertThat(user.getFirstName()).isEqualTo("Ana");
    }

    @Test
    void testReadUnknownUserThrowsNotFound() {
        assertThatThrownBy(() -> this.userService.read(UNKNOWN_USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(NOT_FOUND);
                });
    }

    @Test
    void testFindAllWithoutFilterReturnsSeededUsers() {
        List<User> users = this.userService.find(null);
        assertThat(users).hasSize(2);
    }

    @Test
    void testFindBillableReturnsOnlyBillableUsers() {
        List<User> users = this.userService.find(true);
        assertThat(users).isNotEmpty();
        assertThat(users).allSatisfy(user -> assertThat(user.isBillable()).isTrue());
    }

    @Test
    void testFindNotBillableReturnsOnlyNonBillableUsers() {
        List<User> users = this.userService.find(false);
        assertThat(users).allSatisfy(user -> assertThat(user.isBillable()).isFalse());
    }

    @Test
    void testDeleteExistingUser() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000100"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.user@example.com")
                .identity("10000000C")
                .address("Calle Temporal 1")
                .city("Madrid")
                .province(Province.MADRID)
                .postalCode("28001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        this.userService.delete(user.getId());

        assertThat(this.userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void testDeleteUnknownUserThrowsNotFound() {
        assertThatThrownBy(() -> this.userService.delete(UNKNOWN_USER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(NOT_FOUND);
                });
    }

    @Test
    void testUpdateActiveExistingUser() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000101"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.active@example.com")
                .identity("10000001E")
                .address("Calle Temporal 3")
                .city("Madrid")
                .province(Province.MADRID)
                .postalCode("28001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        User updated = this.userService.updateActive(user.getId(), false);

        assertThat(updated.getActive()).isFalse();
        assertThat(this.userRepository.findById(user.getId()).orElseThrow().getActive()).isFalse();

        this.userRepository.deleteById(user.getId());
    }

    @Test
    void testUpdateActiveUnknownUserThrowsNotFound() {
        assertThatThrownBy(() -> this.userService.updateActive(UNKNOWN_USER_ID, false))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(NOT_FOUND);
                });
    }

    @Test
    void testUpdateExistingUser() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000102"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.update@example.com")
                .identity("10000002G")
                .address("Calle Temporal 5")
                .city("Madrid")
                .province(Province.MADRID)
                .postalCode("28001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        User updatedUser = User.builder()
                .firstName("Updated")
                .familyName("Name")
                .email("updated@example.com")
                .identity("10000003H")
                .address("Calle Nueva 1")
                .city("Barcelona")
                .province(Province.BARCELONA)
                .postalCode("08001")
                .role(Role.ADMIN)
                .build();

        User updated = this.userService.update(user.getId(), updatedUser);

        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getFamilyName()).isEqualTo("Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getProvince()).isEqualTo(Province.BARCELONA);
        assertThat(updated.getRole()).isEqualTo(Role.ADMIN);

        this.userRepository.deleteById(user.getId());
    }

    @Test
    void testUpdateUnknownUserThrowsNotFound() {
        User updatedUser = User.builder()
                .firstName("Updated")
                .familyName("Name")
                .email("updated@example.com")
                .identity("10000003H")
                .address("Calle Nueva 1")
                .city("Barcelona")
                .province(Province.BARCELONA)
                .postalCode("08001")
                .role(Role.ADMIN)
                .build();

        assertThatThrownBy(() -> this.userService.update(UNKNOWN_USER_ID, updatedUser))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(NOT_FOUND);
                });
    }

    @Test
    void testUpdateActiveBatch() {
        User user = this.userRepository.save(User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000103"))
                .firstName("Temporary")
                .familyName("User")
                .email("temporary.batch@example.com")
                .identity("10000004K")
                .address("Calle Temporal 7")
                .city("Madrid")
                .province(Province.MADRID)
                .postalCode("28001")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        List<User> batch = List.of(User.builder()
                .id(user.getId())
                .active(false)
                .build());

        List<User> updated = this.userService.updateActiveBatch(batch);

        assertThat(updated).hasSize(1);
        assertThat(updated.get(0).getActive()).isFalse();
        assertThat(this.userRepository.findById(user.getId()).orElseThrow().getActive()).isFalse();

        this.userRepository.deleteById(user.getId());
    }
}
