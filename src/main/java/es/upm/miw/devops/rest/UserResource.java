package es.upm.miw.devops.rest;

import es.upm.miw.devops.rest.dto.UserDto;
import es.upm.miw.devops.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(UserResource.USERS)
@RequiredArgsConstructor
public class UserResource {

    public static final String USERS = "/users";
    public static final String USER_ID = "/{id}";

    private final UserService userService;

    @GetMapping(USER_ID)
    public UserDto read(@PathVariable UUID id) {
        return UserDto.fromUser(this.userService.read(id));
    }
}
