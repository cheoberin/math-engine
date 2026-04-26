package com.cheobs.math_engine.domain.model.layout;

import java.util.Objects;
import java.util.UUID;

public class Layout {

    private UUID id;
    private LayoutExternalKey externalKey;
    private LayoutName name;
    private LayoutStatus status;

    public Layout(UUID id, String externalKey, String name,  LayoutStatus status) {
        this.id = Objects.requireNonNull(id, "Layout id cannot be null");
        this.externalKey = new LayoutExternalKey(externalKey);
        this.name = new LayoutName(name);
        this.status = Objects.requireNonNull(status, "Layout status cannot be null");
    }

    public Layout(LayoutCommand command) {
        Objects.requireNonNull(command, "LayoutCommand cannot be null");
        this.externalKey = new LayoutExternalKey(command.externalKey());
        this.name = new LayoutName(command.name());
        this.status = LayoutStatus.INACTIVE;
    }

    public void updateDetails(LayoutCommand command) {
        Objects.requireNonNull(command, "LayoutCommand cannot be null");
        this.externalKey = new LayoutExternalKey(command.externalKey());
        this.name = new LayoutName(command.name());
    }

    public void activate(){
        if (this.status == LayoutStatus.ACTIVE) {
            throw new LayoutConflictException("Layout is already active", "conflict.layout.status.active");
        }

        this.status = LayoutStatus.ACTIVE;
    }

    public void deactivate(){
        if (this.status == LayoutStatus.INACTIVE) {
            throw new LayoutConflictException("Layout is already deactivated.", "conflict.layout.status.inactive");
        }

        this.status = LayoutStatus.INACTIVE;
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

    public LayoutStatus getStatus() {
        return status;
    }

}
