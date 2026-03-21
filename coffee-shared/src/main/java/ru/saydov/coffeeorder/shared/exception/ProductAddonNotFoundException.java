package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class ProductAddonNotFoundException extends IdentifiableException {

    public ProductAddonNotFoundException(UUID id) {
        super(id);
    }
}
