package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.common.ConflictException;
import com.cheobs.math_engine.domain.model.common.NotFoundException;
import com.cheobs.math_engine.domain.model.common.ValidationException;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldCommand;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.port.input.FieldUseCase;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FieldService implements FieldUseCase {

    private static final Pattern DEP_PATTERN = Pattern.compile("\\[([A-Za-z0-9]+)]");
    private static final String FIELD_NOT_FOUND_IDENTIFIER = "not-found.field";
    private static final String FIELD_NOT_FOUND_BY_ID_MESSAGE = "Field not found with id: ";
    private static final String LAYOUT_NOT_FOUND_IDENTIFIER = "not-found.layout";
    private static final String LAYOUT_NOT_FOUND_BY_ID_MESSAGE = "Layout not found with id: ";
    private static final String FIELD_EXTERNAL_KEY_CONFLICT_IDENTIFIER = "conflict.field.external-key.in-use";
    private static final String FIELD_EXTERNAL_KEY_CONFLICT_MESSAGE = "Field with external key already exists in layout: ";
    private static final String FIELD_DEPENDENCY_NOT_FOUND_IDENTIFIER = "validation.field.formula.dependency.not-found";
    private static final String FIELD_DEPENDENCY_NOT_FOUND_MESSAGE = "Field formula has unknown dependency: ";
    private static final String FIELD_CYCLE_IDENTIFIER = "conflict.field.circular-dependency";
    private static final String FIELD_CYCLE_MESSAGE = "Cycle detected on: ";

    private final LayoutPort layoutPort;
    private final FieldPort fieldPort;

    public FieldService(LayoutPort layoutPort, FieldPort fieldPort) {
        this.layoutPort = layoutPort;
        this.fieldPort = fieldPort;
    }

    @Override
    @Transactional
    public Field createField(FieldCommand command, UUID layoutId) {
        var layout = layoutPort.getById(layoutId).orElseThrow(() -> new NotFoundException(LAYOUT_NOT_FOUND_BY_ID_MESSAGE + layoutId, LAYOUT_NOT_FOUND_IDENTIFIER));

        fieldPort.getByLayoutIdAndExternalKey(layoutId, command.externalKey()).ifPresent(existing -> {
            throw new ConflictException(FIELD_EXTERNAL_KEY_CONFLICT_MESSAGE + command.externalKey(), FIELD_EXTERNAL_KEY_CONFLICT_IDENTIFIER);
        });

        List<Field> fields = fieldPort.getByLayoutIdAndSearch(layoutId, null);
        PlannedFieldCommand planned = planCommand(fields, command, null);

        Field field = new Field(planned.command(), layout, planned.calculationOrder());
        return fieldPort.save(field);
    }

    @Override
    @Transactional
    public Field updateField(UUID id, FieldCommand command) {
        var field = getFieldOrThrow(id);
        var layoutId = field.getLayout().getId();

        fieldPort.getByLayoutIdAndExternalKey(layoutId, command.externalKey()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException(FIELD_EXTERNAL_KEY_CONFLICT_MESSAGE + command.externalKey(), FIELD_EXTERNAL_KEY_CONFLICT_IDENTIFIER);
            }
        });

        List<Field> fields = fieldPort.getByLayoutIdAndSearch(layoutId, null);
        PlannedFieldCommand planned = planCommand(fields, command, id);

        field.updateDetails(planned.command(), planned.calculationOrder());
        return fieldPort.save(field);
    }

    @Override
    @Transactional(readOnly = true)
    public Field getField(UUID id) {
        return getFieldOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Field> getFieldsByLayoutId(UUID layoutId, String search) {
        layoutPort.getById(layoutId).orElseThrow(() -> new NotFoundException(LAYOUT_NOT_FOUND_BY_ID_MESSAGE + layoutId, LAYOUT_NOT_FOUND_IDENTIFIER));

        search = (search == null || search.isBlank()) ? null : search;
        return fieldPort.getByLayoutIdAndSearch(layoutId, search);
    }

    @Override
    @Transactional
    public void deleteField(UUID id) {
        getFieldOrThrow(id);
        fieldPort.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteFields(List<UUID> ids) {
        ids.forEach(this::getFieldOrThrow);
        fieldPort.deleteByIds(ids);
    }

    private Field getFieldOrThrow(UUID id) {
        return fieldPort.getById(id).orElseThrow(() -> new NotFoundException(FIELD_NOT_FOUND_BY_ID_MESSAGE + id, FIELD_NOT_FOUND_IDENTIFIER));
    }

    private record GraphNode(String externalKey, FieldSource source, String formula) {
    }

    private record PlannedFieldCommand(FieldCommand command, Integer calculationOrder) {
    }

    private PlannedFieldCommand planCommand(List<Field> existingFields, FieldCommand command, UUID updatedId) {
        List<GraphNode> nodes = toGraphNodes(existingFields, command, updatedId);

        validateDependencies(nodes);
        Map<String, Set<String>> graph = buildGraph(nodes);
        sort(graph);
        Map<String, Integer> calculationOrderByKey = assignCalculationOrder(nodes, graph);

        String targetExternalKey = normalizeExternalKey(command.externalKey());
        Integer targetOrder = command.source() == FieldSource.CALCULATION
                ? calculationOrderByKey.get(targetExternalKey)
                : null;

        return new PlannedFieldCommand(command, targetOrder);
    }

    private List<GraphNode> toGraphNodes(List<Field> fields, FieldCommand command, UUID updatedId) {
        List<GraphNode> nodes = new ArrayList<>();
        boolean replaced = false;

        for (Field field : fields) {
            if (updatedId != null && updatedId.equals(field.getId())) {
                nodes.add(new GraphNode(normalizeExternalKey(command.externalKey()), command.source(), normalizeFormula(command.formula())));
                replaced = true;
            } else {
                nodes.add(new GraphNode(field.getExternalKey(), field.getSource(), field.getFormula()));
            }
        }

        if (updatedId == null || !replaced) {
            nodes.add(new GraphNode(normalizeExternalKey(command.externalKey()), command.source(), normalizeFormula(command.formula())));
        }

        return nodes;
    }

    private void validateDependencies(List<GraphNode> nodes) {
        Set<String> existing = new HashSet<>();
        for (GraphNode node : nodes) {
            existing.add(node.externalKey());
        }

        for (GraphNode node : nodes) {
            Set<String> missing = extractDependencies(node.formula())
                    .stream()
                    .filter(dep -> !existing.contains(dep))
                    .collect(java.util.stream.Collectors.toSet());

            if (!missing.isEmpty()) {
                throw new ValidationException(FIELD_DEPENDENCY_NOT_FOUND_MESSAGE + missing.iterator().next(), FIELD_DEPENDENCY_NOT_FOUND_IDENTIFIER);
            }
        }
    }

    private Map<String, Set<String>> buildGraph(List<GraphNode> nodes) {
        Map<String, Set<String>> graph = new HashMap<>();
        Set<String> existing = new HashSet<>();

        for (GraphNode node : nodes) {
            existing.add(node.externalKey());
        }

        for (GraphNode node : nodes) {
            Set<String> deps = extractDependencies(node.formula())
                    .stream()
                    .filter(existing::contains)
                    .collect(java.util.stream.Collectors.toSet());

            graph.put(node.externalKey(), deps);
        }

        return graph;
    }

    private Map<String, Integer> assignCalculationOrder(List<GraphNode> nodes, Map<String, Set<String>> graph) {
        Map<String, FieldSource> sourceByExternalKey = new HashMap<>();
        for (GraphNode node : nodes) {
            sourceByExternalKey.put(node.externalKey(), node.source());
        }

        Map<String, Integer> orderByExternalKey = new HashMap<>();
        Map<String, Integer> levelMemo = new HashMap<>();

        for (String externalKey : graph.keySet()) {
            if (sourceByExternalKey.get(externalKey) == FieldSource.CALCULATION) {
                int level = calculateLevel(externalKey, graph, sourceByExternalKey, levelMemo);
                orderByExternalKey.put(externalKey, level);
            }
        }

        return orderByExternalKey;
    }

    private int calculateLevel(String externalKey,
                               Map<String, Set<String>> graph,
                               Map<String, FieldSource> sourceByExternalKey,
                               Map<String, Integer> levelMemo) {

        if (levelMemo.containsKey(externalKey)) {
            return levelMemo.get(externalKey);
        }

        int maxDependencyLevel = 0;
        for (String dep : graph.getOrDefault(externalKey, Set.of())) {
            int depLevel = calculateLevel(dep, graph, sourceByExternalKey, levelMemo);
            maxDependencyLevel = Math.max(maxDependencyLevel, depLevel);
        }

        int level = sourceByExternalKey.get(externalKey) == FieldSource.CALCULATION
                ? maxDependencyLevel + 1
                : 0;

        levelMemo.put(externalKey, level);
        return level;
    }

    private void sort(Map<String, Set<String>> graph) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        List<String> nodes = new ArrayList<>(graph.keySet());
        nodes.sort(Comparator.naturalOrder());
        for (String node : nodes) {
            dfs(node, graph, visited, visiting, result);
        }
    }

    private void dfs(String node, Map<String, Set<String>> graph, Set<String> visited, Set<String> visiting, List<String> result) {
        if (visited.contains(node)) return;

        if (visiting.contains(node)) {
            throw new ConflictException(FIELD_CYCLE_MESSAGE + node, FIELD_CYCLE_IDENTIFIER);
        }

        visiting.add(node);

        List<String> deps = new ArrayList<>(graph.getOrDefault(node, Set.of()));
        deps.sort(Comparator.naturalOrder());
        for (String dep : deps) {
            dfs(dep, graph, visited, visiting, result);
        }

        visiting.remove(node);
        visited.add(node);
        result.add(node);
    }

    private String normalizeExternalKey(String externalKey) {
        return externalKey == null ? null : externalKey.trim().toUpperCase();
    }

    private String normalizeFormula(String formula) {
        if (formula == null) return null;
        String normalized = formula.trim();
        if (normalized.isEmpty()) return null;
        return normalized.toUpperCase().replace(" ", "");
    }

    private Set<String> extractDependencies(String formula) {
        Set<String> deps = new HashSet<>();
        if (formula == null) return deps;

        Matcher m = DEP_PATTERN.matcher(formula);
        while (m.find()) {
            deps.add(m.group(1).toUpperCase());
        }
        return deps;
    }
}
