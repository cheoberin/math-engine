package com.cheobs.math_engine.domain.port.input;

import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutCommand;

import java.util.List;
import java.util.UUID;

public interface LayoutUseCase {

    Layout createLayout(LayoutCommand command);

    Layout updateLayout(UUID layoutId, LayoutCommand command);

    Layout findLayout(UUID layoutId);

    Layout findLayoutByExternalKey(String externalKey);

    List<Layout> findLayouts(String searchKey);

}
