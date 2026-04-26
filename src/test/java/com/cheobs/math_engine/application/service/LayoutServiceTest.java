package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutCommand;
import com.cheobs.math_engine.domain.model.layout.LayoutConflictException;
import com.cheobs.math_engine.domain.model.layout.LayoutNotFoundException;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LayoutServiceTest {

    @Mock
    private LayoutPort layoutPort;

    @InjectMocks
    private LayoutService layoutService;

    @Test
    void shouldCreateLayoutWhenExternalKeyIsAvailable() {
        LayoutCommand command = new LayoutCommand("ab1", "Main");
        Layout persisted = new Layout(UUID.randomUUID(), "AB1", "Main", LayoutStatus.INACTIVE);

        when(layoutPort.getByExternalKey("ab1")).thenReturn(Optional.empty());
        when(layoutPort.save(any(Layout.class))).thenReturn(persisted);

        Layout result = layoutService.createLayout(command);

        assertSame(persisted, result);

        ArgumentCaptor<Layout> captor = ArgumentCaptor.forClass(Layout.class);
        verify(layoutPort).save(captor.capture());
        assertEquals("AB1", captor.getValue().getExternalKey());
        assertEquals("Main", captor.getValue().getName());
        assertEquals(LayoutStatus.INACTIVE, captor.getValue().getStatus());
    }

    @Test
    void shouldThrowConflictWhenCreatingLayoutWithExistingExternalKey() {
        LayoutCommand command = new LayoutCommand("A1", "Main");
        Layout existing = new Layout(UUID.randomUUID(), "A1", "Existing", LayoutStatus.ACTIVE);

        when(layoutPort.getByExternalKey("A1")).thenReturn(Optional.of(existing));

        LayoutConflictException exception = assertThrows(
                LayoutConflictException.class,
                () -> layoutService.createLayout(command)
        );

        assertEquals("conflict.layout.external-key.in-use", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingLayout() {
        UUID layoutId = UUID.randomUUID();
        LayoutCommand command = new LayoutCommand("A1", "Main");

        when(layoutPort.getById(layoutId)).thenReturn(Optional.empty());

        LayoutNotFoundException exception = assertThrows(
                LayoutNotFoundException.class,
                () -> layoutService.updateLayout(layoutId, command)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingToExternalKeyUsedByAnotherLayout() {
        UUID layoutId = UUID.randomUUID();
        LayoutCommand command = new LayoutCommand("B1", "Updated");

        Layout current = new Layout(layoutId, "A1", "Current", LayoutStatus.INACTIVE);
        Layout other = new Layout(UUID.randomUUID(), "B1", "Other", LayoutStatus.ACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(current));
        when(layoutPort.getByExternalKey("B1")).thenReturn(Optional.of(other));

        LayoutConflictException exception = assertThrows(
                LayoutConflictException.class,
                () -> layoutService.updateLayout(layoutId, command)
        );

        assertEquals("conflict.layout.external-key.in-use", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }

    @Test
    void shouldUpdateLayoutWhenExternalKeyBelongsToSameLayout() {
        UUID layoutId = UUID.randomUUID();
        LayoutCommand command = new LayoutCommand("b1", "Updated Name");

        Layout current = new Layout(layoutId, "A1", "Current", LayoutStatus.INACTIVE);
        Layout sameIdByExternalKey = new Layout(layoutId, "B1", "Current", LayoutStatus.INACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(current));
        when(layoutPort.getByExternalKey("b1")).thenReturn(Optional.of(sameIdByExternalKey));
        when(layoutPort.save(current)).thenReturn(current);

        Layout result = layoutService.updateLayout(layoutId, command);

        assertSame(current, result);
        assertEquals("B1", result.getExternalKey());
        assertEquals("Updated Name", result.getName());
        verify(layoutPort).save(current);
    }

    @Test
    void shouldFindLayoutById() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "A1", "Main", LayoutStatus.ACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));

        Layout result = layoutService.findLayout(layoutId);

        assertSame(layout, result);
    }

    @Test
    void shouldThrowNotFoundWhenFindingLayoutById() {
        UUID layoutId = UUID.randomUUID();

        when(layoutPort.getById(layoutId)).thenReturn(Optional.empty());

        LayoutNotFoundException exception = assertThrows(
                LayoutNotFoundException.class,
                () -> layoutService.findLayout(layoutId)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
    }

    @Test
    void shouldFindLayoutByExternalKey() {
        Layout layout = new Layout(UUID.randomUUID(), "A1", "Main", LayoutStatus.ACTIVE);

        when(layoutPort.getByExternalKey("A1")).thenReturn(Optional.of(layout));

        Layout result = layoutService.findLayoutByExternalKey("A1");

        assertSame(layout, result);
    }

    @Test
    void shouldThrowNotFoundWhenFindingLayoutByExternalKey() {
        when(layoutPort.getByExternalKey("A1")).thenReturn(Optional.empty());

        LayoutNotFoundException exception = assertThrows(
                LayoutNotFoundException.class,
                () -> layoutService.findLayoutByExternalKey("A1")
        );

        assertEquals("not-found.layout", exception.getIdentifier());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldSearchUsingNullWhenSearchKeyIsBlank(String searchKey) {
        List<Layout> layouts = List.of(new Layout(UUID.randomUUID(), "A1", "Main", LayoutStatus.ACTIVE));

        when(layoutPort.getBySearch(null)).thenReturn(layouts);

        List<Layout> result = layoutService.findLayouts(searchKey);

        assertSame(layouts, result);
        verify(layoutPort).getBySearch(null);
    }

    @Test
    void shouldSearchUsingProvidedSearchKey() {
        List<Layout> layouts = List.of(new Layout(UUID.randomUUID(), "A1", "Main", LayoutStatus.ACTIVE));

        when(layoutPort.getBySearch("math")).thenReturn(layouts);

        List<Layout> result = layoutService.findLayouts("math");

        assertSame(layouts, result);
        verify(layoutPort).getBySearch("math");
    }

    @Test
    void shouldActivateLayout() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "A1", "Main", LayoutStatus.INACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(layoutPort.save(layout)).thenReturn(layout);

        layoutService.activateLayout(layoutId);

        assertEquals(LayoutStatus.ACTIVE, layout.getStatus());
        verify(layoutPort).save(layout);
    }

    @Test
    void shouldThrowNotFoundWhenActivatingMissingLayout() {
        UUID layoutId = UUID.randomUUID();

        when(layoutPort.getById(layoutId)).thenReturn(Optional.empty());

        LayoutNotFoundException exception = assertThrows(
                LayoutNotFoundException.class,
                () -> layoutService.activateLayout(layoutId)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }

    @Test
    void shouldThrowConflictWhenActivatingAlreadyActiveLayout() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "A1", "Main", LayoutStatus.ACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));

        LayoutConflictException exception = assertThrows(
                LayoutConflictException.class,
                () -> layoutService.activateLayout(layoutId)
        );

        assertEquals("conflict.layout.status.active", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }

    @Test
    void shouldDeactivateLayout() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "A1", "Main", LayoutStatus.ACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(layoutPort.save(layout)).thenReturn(layout);

        layoutService.deactivateLayout(layoutId);

        assertEquals(LayoutStatus.INACTIVE, layout.getStatus());
        verify(layoutPort).save(layout);
    }

    @Test
    void shouldThrowNotFoundWhenDeactivatingMissingLayout() {
        UUID layoutId = UUID.randomUUID();

        when(layoutPort.getById(layoutId)).thenReturn(Optional.empty());

        LayoutNotFoundException exception = assertThrows(
                LayoutNotFoundException.class,
                () -> layoutService.deactivateLayout(layoutId)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }

    @Test
    void shouldThrowConflictWhenDeactivatingAlreadyInactiveLayout() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "A1", "Main", LayoutStatus.INACTIVE);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));

        LayoutConflictException exception = assertThrows(
                LayoutConflictException.class,
                () -> layoutService.deactivateLayout(layoutId)
        );

        assertEquals("conflict.layout.status.inactive", exception.getIdentifier());
        verify(layoutPort, never()).save(any(Layout.class));
    }
}

