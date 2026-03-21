package ru.saydov.coffeeorder.shared.exception;

import java.util.UUID;

public class VenueNotFoundException extends IdentifiableException {

    public VenueNotFoundException(UUID id) {
        super(id);
    }
}
