package br.com.teste.demo.repositories;

import br.com.teste.demo.enums.Role;
import br.com.teste.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByAtivo(Boolean ativo);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
