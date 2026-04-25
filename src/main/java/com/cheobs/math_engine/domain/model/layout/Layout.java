package com.cheobs.math_engine.domain.model.layout;

import java.util.UUID;

public class Layout {

    UUID id;
    LayoutExternalKey externalKey;
    LayoutName name;

    public Layout(UUID id, String externalKey, String name) {
        this.id = id;
        this.externalKey = new LayoutExternalKey(externalKey);
        this.name = new LayoutName(name);
    }

    public Layout(LayoutCommand command) {
        this.externalKey = new LayoutExternalKey(command.externalKey());
        this.name = new LayoutName(command.name());
    }

    public void applyCommand(LayoutCommand command) {
        this.externalKey = new LayoutExternalKey(command.externalKey());
        this.name = new LayoutName(command.name());
    }

    public UUID getId() {
        return id;
    }

    public String getExternalKey() {
        return externalKey.value();
    }

    public String getName() {
        return name.value();
    }
}
