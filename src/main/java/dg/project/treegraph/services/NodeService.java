package dg.project.treegraph.services;

import dg.project.treegraph.dto.NodeDTO;

import java.util.List;
import java.util.Optional;

public interface NodeService {
    List<NodeDTO> getAll();

    Optional<NodeDTO> getById(Long id);

    void add(NodeDTO dto);

    void remove(Long id);

    void update(NodeDTO dto);
}
