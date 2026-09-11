package es.upm.miw.devops.configurations;

import es.upm.miw.devops.domain.model.Province;
import es.upm.miw.devops.domain.model.Role;
import es.upm.miw.devops.domain.model.User;
import es.upm.miw.devops.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class SeederForDev implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (this.userRepository.count() > 0) {
            return;
        }
        this.seedUsers();
    }

    private void seedUsers() {
        User admin = User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
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
        User customer = User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .firstName("Luis")
                .familyName("Perez")
                .email("luis.perez@example.com")
                .identity("87654321B")
                .address("Avenida del Sol 10")
                .city("Barcelona")
                .province(Province.BARCELONA)
                .postalCode("08001")
                .role(Role.CUSTOMER)
                .active(true)
                .build();
        this.userRepository.save(admin);
        this.userRepository.save(customer);
    }
}
