package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutCommand;
import com.cheobs.math_engine.domain.model.layout.LayoutConflictException;
import com.cheobs.math_engine.domain.model.layout.LayoutNotFoundException;
import com.cheobs.math_engine.domain.port.input.LayoutUseCase;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LayoutService implements LayoutUseCase {

    private final LayoutPort layoutPort;

    public LayoutService(LayoutPort layoutPort) {
        this.layoutPort = layoutPort;
    }

    @Override
    @Transactional
    public Layout createLayout(LayoutCommand command) {

        layoutPort.getByExternalKey(command.externalKey()).ifPresent(existing -> {
            throw new LayoutConflictException("Layout with external key already exists: " + command.externalKey(), "conflict.layout.external-key.in-use");
        });

        Layout layout = new Layout(command);
        layout = layoutPort.save(layout);
        return layout;
    }

    @Override
    @Transactional
    public Layout updateLayout(UUID layoutId, LayoutCommand command) {
        var optionalLayout = layoutPort.getById(layoutId);
        var layout = optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with id: " + layoutId, "not-found.layout"));

        layoutPort.getByExternalKey(command.externalKey()).ifPresent(existing -> {
            if (!existing.getId().equals(layoutId)) {
                throw new LayoutConflictException("Layout with external key already exists: " + command.externalKey(), "conflict.layout.external-key.in-use");
            }
        });

        layout.updateDetails(command);
        layout = layoutPort.save(layout);
        return layout;
    }

    @Override
    @Transactional(readOnly = true)
    public Layout findLayout(UUID layoutId) {
        var optionalLayout = layoutPort.getById(layoutId);
        return optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with id: " + layoutId, "not-found.layout"));
    }

    @Override
    @Transactional(readOnly = true)
    public Layout findLayoutByExternalKey(String externalKey) {
        var optionalLayout = layoutPort.getByExternalKey(externalKey);
        return optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with external key: " + externalKey, "not-found.layout"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Layout> findLayouts(String searchKey) {
        searchKey = (searchKey == null || searchKey.isBlank()) ? null : searchKey;
        return layoutPort.getBySearch(searchKey);
    }

    @Override
    @Transactional
    public void activateLayout(UUID layoutId) {
        var optionalLayout = layoutPort.getById(layoutId);
        var layout = optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with id: " + layoutId, "not-found.layout"));
        layout.activate();
        layoutPort.save(layout);
    }

    @Override
    @Transactional
    public void deactivateLayout(UUID layoutId) {
        var optionalLayout = layoutPort.getById(layoutId);
        var layout = optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with id: " + layoutId, "not-found.layout"));
        layout.deactivate();
        layoutPort.save(layout);
    }
}
