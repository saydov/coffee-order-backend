package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class ClientNotFoundException extends IdentifiableException {

    public ClientNotFoundException(UUID id) {
        super(id);
    }
}
