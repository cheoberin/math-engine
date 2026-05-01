package com.cheobs.math_engine.domain.model.layout;

import com.cheobs.math_engine.domain.model.common.exceptions.ConflictException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LayoutTest {

    @Test
    void shouldCreateLayoutFromCompleteConstructor() {
        UUID id = UUID.randomUUID();

        Layout layout = new Layout(id, " ab1 ", "  Main Layout  ", LayoutStatus.ACTIVE);

        assertEquals(id, layout.getId());
        assertEquals("AB1", layout.getExternalKey());
        assertEquals("Main Layout", layout.getName());
        assertEquals(LayoutStatus.ACTIVE, layout.getStatus());
    }

    @Test
    void shouldCreateLayoutFromCommandAsInactive() {
        Layout layout = new Layout(new LayoutCommand(" c1 ", "  Secondary  "));

        assertEquals("C1", layout.getExternalKey());
        assertEquals("Secondary", layout.getName());
        assertEquals(LayoutStatus.INACTIVE, layout.getStatus());
    }

    @Test
    void shouldUpdateLayoutDetails() {
        Layout layout = new Layout(UUID.randomUUID(), "A1", "Original", LayoutStatus.INACTIVE);

        layout.updateDetails(new LayoutCommand(" b2 ", " Updated Name "));

        assertEquals("B2", layout.getExternalKey());
        assertEquals("Updated Name", layout.getName());
    }

    @Test
    void shouldActivateLayout() {
        Layout layout = new Layout(UUID.randomUUID(), "A1", "Original", LayoutStatus.INACTIVE);

        layout.activate();

        assertEquals(LayoutStatus.ACTIVE, layout.getStatus());
    }

    @Test
    void shouldThrowWhenActivatingAlreadyActiveLayout() {
        Layout layout = new Layout(UUID.randomUUID(), "A1", "Original", LayoutStatus.ACTIVE);

        ConflictException exception = assertThrows(ConflictException.class, layout::activate);

        assertEquals("conflict.layout.status.active", exception.getIdentifier());
    }

    @Test
    void shouldDeactivateLayout() {
        Layout layout = new Layout(UUID.randomUUID(), "A1", "Original", LayoutStatus.ACTIVE);

        layout.deactivate();

        assertEquals(LayoutStatus.INACTIVE, layout.getStatus());
    }

    @Test
    void shouldThrowWhenDeactivatingAlreadyInactiveLayout() {
        Layout layout = new Layout(UUID.randomUUID(), "A1", "Original", LayoutStatus.INACTIVE);

        ConflictException exception = assertThrows(ConflictException.class, layout::deactivate);

        assertEquals("conflict.layout.status.inactive", exception.getIdentifier());
    }
}

