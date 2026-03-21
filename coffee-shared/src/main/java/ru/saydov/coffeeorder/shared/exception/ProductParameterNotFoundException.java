package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class ProductParameterNotFoundException extends IdentifiableException {

    public ProductParameterNotFoundException(UUID id) {
        super(id);
    }
}
