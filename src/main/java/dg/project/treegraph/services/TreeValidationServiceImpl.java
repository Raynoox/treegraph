package dg.project.treegraph.services;

import dg.project.treegraph.models.Node;
import dg.project.treegraph.services.exceptions.GraphIsNotATreeException;
import dg.project.treegraph.services.exceptions.TreeHasCyclesException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TreeValidationServiceImpl implements TreeValidationService {

    private final Map<Long, VisitedWrapper> visitedMap = new HashMap<>();
    @Override
    public void validateTree(List<Node> nodes) {
        checkInitialTreeConstraints(nodes);

        initializeVisitedMap(nodes);

        for(VisitedWrapper node : visitedMap.values()) {
            DFS2(node);
        }

    }

    private void DFS2(VisitedWrapper node) {
        if(node != null && !isVisited(node.id)) {
            if(isVisitedThisIteration(node.id)) {
                throw new TreeHasCyclesException();
            }
            markNodeVisitedThisIteration(node.id);
            DFS2(getWrapperById(node.parentId));
            markNodeVisited(node.id);
        }
    }

    private void checkInitialTreeConstraints(List<Node> nodes) {
        long rootCount = nodes.stream().filter(node -> node.getParent() == null).count();
        evaluate(rootCount);
    }

    private void evaluate(long rootNumbers) {
        if(rootNumbers > 1) {
            throw new GraphIsNotATreeException();
        }
        if(rootNumbers == 0) {
            throw new TreeHasCyclesException();
        }
    }

    private void initializeVisitedMap(List<Node> nodes) {
        nodes.forEach(node -> visitedMap.put(node.getId(), new VisitedWrapper(node)));
    }

    private boolean isVisitedThisIteration(Long nodeId) {
        return visitedMap.get(nodeId).isVisitedThisIteration();
    }
    private boolean isVisited(Long nodeId) {
        return nodeId == null || visitedMap.get(nodeId).isVisited();
    }
    private VisitedWrapper getWrapperById(Long id) {
        return visitedMap.get(id);
    }
    private void markNodeVisitedThisIteration(Long nodeId) {
        visitedMap.get(nodeId).setVisitedThisIteration(true);
    }
    private void markNodeVisited(Long nodeId) {
        visitedMap.get(nodeId).setVisited(true);
    }
    @Getter
    @Setter
    public class VisitedWrapper{
        boolean isVisited;
        boolean visitedThisIteration;
        Long id;
        Long parentId;
        VisitedWrapper(Node node) {
            id = node.getId();
            parentId = node.getParent() != null ? node.getParent().getId() : null;
        }
    }
}
