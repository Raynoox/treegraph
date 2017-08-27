package dg.project.treegraph.services.exceptions;

public class TreeHasCyclesException extends TreeValidationException {
    public TreeHasCyclesException() {
        super("Cannot perform operation because it will create cycles");
    }
}
