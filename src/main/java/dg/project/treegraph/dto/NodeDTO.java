package dg.project.treegraph.dto;

import dg.project.treegraph.models.Node;
import lombok.Data;

@Data
public class NodeDTO {
    private Long id;
    private Long parentId;
    private Long value;

    public NodeDTO() {

    }
    public NodeDTO(Node node) {
        this.id = node.getId();
        this.parentId = node.getParent() != null ? node.getParent().getId() : null;
        this.value = node.getValue();
    }
}
