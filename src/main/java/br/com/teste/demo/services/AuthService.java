package br.com.teste.demo.services;

import br.com.teste.demo.dtos.LoginRequest;
import br.com.teste.demo.dtos.LoginResponse;
import br.com.teste.demo.dtos.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    LoginResponse authenticateUser(LoginRequest loginRequest);

    String registerUser(RegisterRequest registerRequest);
}
