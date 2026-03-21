package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class OrderNotFoundException extends IdentifiableException {

    public OrderNotFoundException(UUID id) {
        super(id);
    }
}
