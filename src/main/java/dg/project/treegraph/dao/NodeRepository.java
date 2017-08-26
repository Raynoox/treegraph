package dg.project.treegraph.dao;

import dg.project.treegraph.models.Node;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends CrudRepository<Node, Long>{
    List<Node> findAll();
    List<Node> findAllByParent_Id(Long id);
}
