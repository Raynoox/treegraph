package dg.project.treegraph.services.exceptions;

public abstract class TreeValidationException extends RuntimeException{
    TreeValidationException(String s) {
        super(s);
    }
}
