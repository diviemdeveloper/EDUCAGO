package es.safareyes.educago.service;

import es.safareyes.educago.dto.request.LoginRequest;
import es.safareyes.educago.dto.request.RegistroRequest;
import es.safareyes.educago.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse registrar(RegistroRequest request);
    AuthResponse login(LoginRequest request);
}