package br.com.teste.demo.services.impl;

import br.com.teste.demo.dtos.LoginRequest;
import br.com.teste.demo.dtos.LoginResponse;
import br.com.teste.demo.dtos.RegisterRequest;
import br.com.teste.demo.exceptions.BusinessException;
import br.com.teste.demo.models.User;
import br.com.teste.demo.repositories.UserRepository;
import br.com.teste.demo.security.JwtTokenProvider;
import br.com.teste.demo.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new LoginResponse(jwt, user.getUsername(), user.getRole().getAuthority());
    }

    @Override
    @Transactional
    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException("Erro: Username já está em uso!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("Erro: Email já está em uso!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setNomeCompleto(registerRequest.getNomeCompleto());
        user.setRole(registerRequest.getRole());
        user.setAtivo(true);
        user.setDataCadastro(LocalDateTime.now());
        user.setDataAtualizacao(LocalDateTime.now());

        userRepository.save(user);

        return "Usuário registrado com sucesso!";
    }
}
