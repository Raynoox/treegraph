package dg.project.treegraph.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "NODE")
public class Node {
    @Id @GeneratedValue
    private Long id;
    @Column(name= "VALUE", nullable = false)
    private Long value;
    @ManyToOne(fetch=FetchType.EAGER)
    private Node parent;


    public Node() {

    }

    public void setParentId(Long id) {
        Node parent = new Node();
        parent.setId(id);
        this.setParent(parent);
    }

    private Node(Builder builder) {
        setValue(builder.value);
        setParent(builder.parent);
    }

    public static final class Builder {
        private Long value;
        private Node parent;

        public Builder() {
        }

        public Builder value(Long val) {
            value = val;
            return this;
        }

        public Builder parent(Node val) {
            parent = val;
            return this;
        }
        public Builder parentId(Long id) {
            if(id != null) {
                parent = new Node();
                parent.setId(id);
            }
            return this;
        }
        public Node build() {
            return new Node(this);
        }

    }
}
