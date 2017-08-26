package dg.project.treegraph.services;

import dg.project.treegraph.dao.NodeRepository;
import dg.project.treegraph.dto.NodeDTO;
import dg.project.treegraph.models.Node;
import dg.project.treegraph.services.exceptions.GraphIsNotATreeException;
import dg.project.treegraph.services.exceptions.TreeHasCyclesException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NodeServiceImpl.class)
public class NodeServiceImplTest {
    private static final Long NODE_ID_1 = 1L;
    private static final Long NODE_ID_2 = 2L;
    private static final Long NODE_ID_3 = 3L;
    private static final Long NODE_ID_4 = 4L;
    private static final Long NODE_VALUE_1 = 10L;
    private static final Long NODE_VALUE_2 = 20L;
    private static final Long NODE_PARENT_1 = 2L;
    private static final Long NODE_PARENT_2 = 1L;
    @MockBean
    NodeRepository repository;

    @MockBean
    TreeValidationService treeValidationService;
    @Autowired
    NodeService service;

    @Test
    public void testGetAllEmptyRepository() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(0, service.getAll().size());
        verify(repository, times(1)).findAll();

    }

    @Test
    public void testGetAll() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        List<NodeDTO> result = service.getAll();
        assertEquals(4, result.size());
        assertEquals(NODE_ID_1, result.get(0).getId());
        assertEquals(NODE_ID_2, result.get(1).getId());
        assertEquals(NODE_ID_3, result.get(2).getId());
        assertEquals(NODE_ID_4, result.get(3).getId());
        verify(repository, times(1)).findAll();

    }
    @Test
    public void testGetByIdReturnsNode() throws Exception {
        when(repository.findOne(NODE_ID_1)).thenReturn(singleRoot());
        Optional<NodeDTO> result = service.getById(NODE_ID_1);
        assertTrue(result.isPresent());
        assertEquals(NODE_ID_1, result.get().getId());
        assertNull(result.get().getParentId());
        assertEquals(NODE_VALUE_1, result.get().getValue());
        verify(repository, times(1)).findOne(NODE_ID_1);
    }
    @Test
    public void testGetByIdDoesNotExists() throws Exception {
        when(repository.findOne(NODE_ID_1)).thenReturn(null);
        Optional<NodeDTO> result = service.getById(NODE_ID_1);
        assertFalse(result.isPresent());
        verify(repository, times(1)).findOne(NODE_ID_1);
    }
    @Test(expected = GraphIsNotATreeException.class)
    public void testAddExceptionWhenCreatingOtherTree() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        doThrow(new GraphIsNotATreeException()).when(treeValidationService).validateTree(aTree());
        service.add(new NodeDTO(singleRoot()));
        verify(treeValidationService, times(1)).validateTree(aTree());

    }
    @Test(expected = TreeHasCyclesException.class)
    public void testAddExceptionWhenHasCycles() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        doThrow(new TreeHasCyclesException()).when(treeValidationService).validateTree(aTree());
        service.add(new NodeDTO(singleRoot()));
        verify(treeValidationService, times(1)).validateTree(aTree());

    }

    @Test
    public void testAddSuccess() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        NodeDTO dto = new NodeDTO(aNodeWithoutId());
        service.add(dto);
        verify(repository,times(1)).save(aNodeWithoutId());
        verify(treeValidationService, times(1)).validateTree(aTree());
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testAddNullValue() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        NodeDTO dto = new NodeDTO(aNodeWithoutValue());
        doThrow(DataIntegrityViolationException.class).when(repository).save(aNodeWithoutValue());
        service.add(dto);
        verify(repository, times(1)).save(aNodeWithoutValue());
    }
    @Test
    public void testSuccessRemove() throws Exception {
        List<Node> children = aTree();
        when(repository.findAllByParent_Id(NODE_ID_1)).thenReturn(children);
        when(repository.findOne(NODE_ID_1)).thenReturn(singleNode(NODE_ID_1));
        service.remove(NODE_ID_1);
        verify(repository,times(1)).delete(NODE_ID_1);
        for(Node child : children) {
            assertEquals(NODE_PARENT_1, child.getParent().getId());
        }
    }
    @Test(expected = GraphIsNotATreeException.class)
    public void testRemoveCreatesTwoTrees() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        doThrow(new GraphIsNotATreeException()).when(treeValidationService).validateTree(aTree());
        service.remove(NODE_ID_2);
    }
    @Test(expected = TreeHasCyclesException.class)
    public void testRemoveCreatesCycle() throws Exception {
        when(repository.findAll()).thenReturn(aTree());
        doThrow(new TreeHasCyclesException()).when(treeValidationService).validateTree(aTree());
        service.remove(NODE_ID_2);
    }
    @Test(expected = TreeHasCyclesException.class)
    public void testUpdateCreatesCycle() throws Exception {
        when(repository.findOne(NODE_ID_2)).thenReturn(singleNode(NODE_ID_2));
        when(repository.findAll()).thenReturn(aTree());
        doThrow(new TreeHasCyclesException()).when(treeValidationService).validateTree(aTree());
        service.update(new NodeDTO(singleNode(NODE_ID_2)));
        verify(repository.save(singleNode(NODE_ID_2)));
    }
    @Test(expected = GraphIsNotATreeException.class)
    public void testUpdateCreatesTwoTrees() throws Exception {
        when(repository.findOne(NODE_ID_2)).thenReturn(singleNode(NODE_ID_2));
        when(repository.findAll()).thenReturn(aTree());
        doThrow(new GraphIsNotATreeException()).when(treeValidationService).validateTree(aTree());
        service.update(new NodeDTO(singleNode(NODE_ID_2)));
        verify(repository.save(singleNode(NODE_ID_2)));
    }

    @Test
    public void testUpdate() throws Exception {
        Node node = singleNode(NODE_ID_2);
        when(repository.findOne(NODE_ID_2)).thenReturn(singleNode(NODE_ID_2));
        NodeDTO dto = new NodeDTO(singleNode(NODE_ID_2));
        dto.setValue(NODE_VALUE_1);
        dto.setParentId(NODE_PARENT_2);

        service.update(dto);

        node.setValue(NODE_VALUE_1);
        node.setParentId(NODE_PARENT_2);

        verify(repository, times(1)).save(node);

    }
    private List<Node> aTree() {
        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();
        Node node4 = new Node();
        node1.setParent(null);
        node2.setParent(node1);
        node3.setParent(node1);
        node4.setParent(node2);
        node1.setId(NODE_ID_1);
        node2.setId(NODE_ID_2);
        node3.setId(NODE_ID_3);
        node4.setId(NODE_ID_4);
        return Arrays.asList(node1, node2, node3, node4);
    }

    private Node singleRoot() {
        Node result = new Node.Builder().parentId(null).value(NODE_VALUE_1).build();
        result.setId(NODE_ID_1);
        return result;
    }

    private Node singleNode(Long id) {
        Node result = new Node.Builder().parentId(NODE_PARENT_1).value(NODE_VALUE_2).build();
        result.setId(id);
        return result;
    }

    private Node aNodeWithoutId() {
        return new Node.Builder().parentId(NODE_ID_1).value(NODE_VALUE_1).build();
    }
    private Node aNodeWithoutValue() {
        return new Node.Builder().parentId(NODE_ID_1).build();
    }
}