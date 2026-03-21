package ru.saydov.coffeeorder.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.saydov.coffeeorder.client.dto.auth.AuthResponse;
import ru.saydov.coffeeorder.client.dto.auth.SendCodeRequest;
import ru.saydov.coffeeorder.client.dto.auth.VerifyCodeRequest;
import ru.saydov.coffeeorder.client.security.JwtProvider;
import ru.saydov.coffeeorder.client.service.VerificationCodeService;
import ru.saydov.coffeeorder.shared.repository.ClientRepository;
import ru.saydov.coffeeorder.shared.service.ClientService;

/**
 * Контроллер авторизации клиента по SMS.
 *
 * <p>Двухшаговый flow: запрос кода ({@code /send-code}) →
 * подтверждение ({@code /verify}) с получением JWT. Флаг
 * {@code newUser} в ответе помогает фронту различать регистрацию
 * от обычного входа и показать онбординг при необходимости.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client/auth")
public class AuthController {

    private final VerificationCodeService verificationCodeService;
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/send-code")
    public void sendCode(@Valid @RequestBody SendCodeRequest request) {
        verificationCodeService.generateAndStore(request.getPhone());
    }

    @PostMapping("/verify")
    public AuthResponse verify(@Valid @RequestBody VerifyCodeRequest request) {
        if (!verificationCodeService.verify(request.getPhone(), request.getCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "code=invalid");
        }

        var isNew = !clientRepository.existsByPhone(request.getPhone());
        var client = clientService.findOrCreateByPhone(request.getPhone());
        var token = jwtProvider.generateToken(client.getId());

        return AuthResponse.builder()
                .token(token)
                .newUser(isNew)
                .build();
    }
}
