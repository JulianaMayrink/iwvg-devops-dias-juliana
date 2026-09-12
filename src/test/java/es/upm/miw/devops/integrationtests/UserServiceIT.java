package es.upm.miw.devops.integrationtests;

import es.upm.miw.devops.domain.model.User;
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
}
