package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class CategoryNotFoundException extends IdentifiableException {

    public CategoryNotFoundException(UUID id) {
        super(id);
    }
}
