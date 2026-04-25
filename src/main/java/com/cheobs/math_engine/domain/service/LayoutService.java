package com.cheobs.math_engine.domain.service;

import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutCommand;
import com.cheobs.math_engine.domain.model.layout.LayoutNotFoundException;
import com.cheobs.math_engine.domain.port.input.LayoutUseCase;
import com.cheobs.math_engine.domain.port.output.LayoutPort;

import java.util.List;
import java.util.UUID;

public class LayoutService implements LayoutUseCase {

    private final LayoutPort layoutPort;

    public LayoutService(LayoutPort layoutPort) {
        this.layoutPort = layoutPort;
    }

    @Override
    public Layout createLayout(LayoutCommand command) {
        Layout layout = new Layout(command);
        layout = layoutPort.save(layout);
        return layout;
    }

    @Override
    public Layout updateLayout(UUID layoutId, LayoutCommand command) {
        var optionalLayout = layoutPort.getById(layoutId);
        var layout = optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with id: " + layoutId, "not-found.layout"));
        layout.applyCommand(command);
        layout = layoutPort.save(layout);
        return layout;
    }

    @Override
    public Layout findLayout(UUID layoutId) {
        var optionalLayout = layoutPort.getById(layoutId);
        return optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with id: " + layoutId, "not-found.layout"));
    }

    @Override
    public Layout findLayoutByExternalKey(String externalKey) {
        var optionalLayout = layoutPort.getByExternalKey(externalKey);
        return optionalLayout.orElseThrow(() -> new LayoutNotFoundException("Layout not found with external key: " + externalKey, "not-found.layout"));
    }

    @Override
    public List<Layout> findLayouts(String searchKey) {
        return layoutPort.getBySearch(searchKey);
    }
}
