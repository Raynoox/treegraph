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
        for(Node node : nodes) {
            DFS(node);
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
        nodes.forEach(node -> visitedMap.put(node.getId(), new VisitedWrapper()));
    }

    private void DFS(Node node) {
        if(!isVisited(node)) {
            if(isVisitedThisIteration(node)) {
                throw new TreeHasCyclesException();
            }
            markNodeVisitedThisIteration(node);
            DFS(node.getParent());
            markNodeVisited(node);
        }
    }
    private boolean isVisitedThisIteration(Node node) {
        return visitedMap.get(node.getId()).isVisitedThisIteration();
    }
    private boolean isVisited(Node node) {
        return node == null || visitedMap.get(node.getId()).isVisited();
    }
    private void markNodeVisitedThisIteration(Node node) {
        visitedMap.get(node.getId()).setVisitedThisIteration(true);
    }
    private void markNodeVisited(Node node) {
        visitedMap.get(node.getId()).setVisited(true);
    }
    @Getter
    @Setter
    public class VisitedWrapper{
        boolean isVisited;
        boolean visitedThisIteration;
    }
}
