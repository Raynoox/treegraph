package dg.project.treegraph.services;

import dg.project.treegraph.models.Node;

import java.util.List;

public interface TreeValidationService {
    void validateTree(List<Node> nodes);
}
