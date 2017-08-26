package dg.project.treegraph.services;

import dg.project.treegraph.dao.NodeRepository;
import dg.project.treegraph.dto.NodeDTO;
import dg.project.treegraph.models.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {

    private final NodeRepository repository;
    private final TreeValidationService treeValidationService;
    @Autowired
    public NodeServiceImpl(NodeRepository repository, TreeValidationService treeValidationService) {
        this.repository = repository;
        this.treeValidationService = treeValidationService;
    }

    @Override
    public List<NodeDTO> getAll() {
        return repository.findAll().stream().map(NodeDTO::new).collect(Collectors.toList());
    }

    @Override
    public Optional<NodeDTO> getById(Long id) {
        Optional<Node> node = Optional.ofNullable(repository.findOne(id));
        return node.map(NodeDTO::new);
    }

    @Override
    @Transactional
    public void add(NodeDTO dto) {
        repository.save(new Node.Builder().parentId(dto.getParentId()).value(dto.getValue()).build());
        validateTree();
    }

    @Override
    @Transactional
    public void remove(Long id) {
        List<Node> children = getAllChildrenOf(id);
        Node node = repository.findOne(id);
        children.forEach(child -> child.setParent(node.getParent()));
        repository.save(children);
        repository.delete(id);
        validateTree();
    }

    @Override
    @Transactional
    public void update(NodeDTO dto) {
        Node node = repository.findOne(dto.getId());
        if(dto.getValue() != null ) {
            node.setValue(dto.getValue());
        }
        if(dto.getParentId() != null) {
            node.setParentId(dto.getParentId());
        }
        repository.save(node);
        validateTree();
    }

    private void validateTree() {
        treeValidationService.validateTree(repository.findAll());
    }
    private List<Node> getAllChildrenOf(Long id) {
        return repository.findAllByParent_Id(id);
    }
}
