package es.upm.miw.devops.rest;

import es.upm.miw.devops.domain.model.User;
import es.upm.miw.devops.rest.dto.UserActiveItem;
import es.upm.miw.devops.rest.dto.UserActiveRequest;
import es.upm.miw.devops.rest.dto.UserDto;
import es.upm.miw.devops.rest.dto.UserUpdateRequest;
import es.upm.miw.devops.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(UserResource.USERS)
@RequiredArgsConstructor
public class UserResource {

    public static final String USERS = "/users";
    public static final String USER_ID = "/{id}";
    public static final String USER_ID_ACTIVE = "/{id}/active";

    private final UserService userService;

    @GetMapping
    public List<UserDto> find(@RequestParam(required = false) Boolean billable) {
        return this.userService.find(billable).stream()
                .map(UserDto::fromUser)
                .toList();
    }

    @GetMapping(USER_ID)
    public UserDto read(@PathVariable UUID id) {
        return UserDto.fromUser(this.userService.read(id));
    }

    @DeleteMapping(USER_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        this.userService.delete(id);
    }

    @PutMapping(USER_ID_ACTIVE)
    public UserDto updateActive(@PathVariable UUID id, @RequestBody UserActiveRequest request) {
        return UserDto.fromUser(this.userService.updateActive(id, request.getActive()));
    }

    @PutMapping(USER_ID)
    public UserDto update(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .familyName(request.getFamilyName())
                .email(request.getEmail())
                .identity(request.getIdentity())
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .role(request.getRole())
                .build();
        return UserDto.fromUser(this.userService.update(id, user));
    }

    @PatchMapping
    public List<UserDto> updateActiveBatch(@RequestBody List<UserActiveItem> request) {
        List<User> users = request.stream()
                .map(item -> User.builder()
                        .id(item.getId())
                        .active(item.getActive())
                        .build())
                .toList();
        return this.userService.updateActiveBatch(users).stream()
                .map(UserDto::fromUser)
                .toList();
    }
}
