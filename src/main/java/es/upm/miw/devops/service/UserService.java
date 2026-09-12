package es.upm.miw.devops.service;

import es.upm.miw.devops.domain.model.User;
import es.upm.miw.devops.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User read(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    public List<User> find(Boolean billable) {
        if (billable == null) {
            return this.userRepository.findAll();
        }
        return this.userRepository.findAll().stream()
                .filter(user -> user.isBillable() == billable)
                .toList();
    }

    public void delete(UUID id) {
        User user = this.read(id);
        this.userRepository.delete(user);
    }

    public User updateActive(UUID id, boolean active) {
        User user = this.read(id);
        user.setActive(active);
        return this.userRepository.save(user);
    }
}
