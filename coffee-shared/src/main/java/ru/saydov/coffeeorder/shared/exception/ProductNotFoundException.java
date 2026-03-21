package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class ProductNotFoundException extends IdentifiableException {

    public ProductNotFoundException(UUID id) {
        super(id);
    }
}
