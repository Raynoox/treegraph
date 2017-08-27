package dg.project.treegraph.services.exceptions;

public class GraphIsNotATreeException extends TreeValidationException {
    public GraphIsNotATreeException() {
        super("Cannot perform operation because it will break the tree");
    }
}
