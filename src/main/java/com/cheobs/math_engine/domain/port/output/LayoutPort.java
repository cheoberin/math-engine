package com.cheobs.math_engine.domain.port.output;

import com.cheobs.math_engine.domain.model.layout.Layout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LayoutPort {

    Layout save(Layout layout);

    Optional<Layout> getById(UUID id);

    Optional<Layout> getByExternalKey(String externalKey);

    List<Layout> getBySearch(String name);
}
