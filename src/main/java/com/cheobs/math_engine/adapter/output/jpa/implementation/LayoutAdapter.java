package com.cheobs.math_engine.adapter.output.jpa.implementation;

import com.cheobs.math_engine.adapter.output.jpa.entity.LayoutEntity;
import com.cheobs.math_engine.adapter.output.jpa.repository.LayoutRepository;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LayoutAdapter implements LayoutPort {

    private final LayoutRepository layoutRepository;

    public LayoutAdapter(LayoutRepository layoutRepository) {
        this.layoutRepository = layoutRepository;
    }

    @Override
    public Layout save(Layout layout) {
        var entity = toEntity(layout);
        entity = layoutRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Layout> getById(UUID id) {
        return layoutRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Layout> getByExternalKey(String externalKey) {
        return layoutRepository.findByExternalKey(externalKey).map(this::toDomain);
    }

    @Override
    public List<Layout> getBySearch(String search) {

        if (search == null || search.isBlank()) {
            return layoutRepository.findAll().stream().map(this::toDomain).toList();
        }

        return layoutRepository
                .findByExternalKeyContainingIgnoreCaseOrNameContainingIgnoreCase(search, search)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private LayoutEntity toEntity(Layout layout) {
        return new LayoutEntity(
                layout.getId(),
                layout.getExternalKey(),
                layout.getName(),
                layout.getStatus()
        );
    }

    private Layout toDomain(LayoutEntity layoutEntity) {
        return new Layout(
                layoutEntity.getId(),
                layoutEntity.getExternalKey(),
                layoutEntity.getName(),
                layoutEntity.getStatus()
        );
    }

}
