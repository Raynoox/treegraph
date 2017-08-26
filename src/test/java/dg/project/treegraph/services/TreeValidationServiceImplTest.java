package dg.project.treegraph.services;

import dg.project.treegraph.models.Node;
import dg.project.treegraph.services.exceptions.GraphIsNotATreeException;
import dg.project.treegraph.services.exceptions.TreeHasCyclesException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TreeValidationServiceImpl.class)
public class TreeValidationServiceImplTest {
    private static final Long NODE_ID_1 = 1L;
    private static final Long NODE_ID_2 = 2L;
    private static final Long NODE_ID_3 = 3L;
    private static final Long NODE_ID_4 = 4L;
    @Autowired
    TreeValidationService service;

    private List<Node> nodes = new ArrayList<>();
    @Test(expected = TreeHasCyclesException.class)
    public void treeWithCycleThrowsException() throws Exception {
        givenTreeWithCycle();
        service.validateTree(nodes);
    }
    @Test
    public void treeWithoutCyclesNoException() throws Exception {
        givenCorrectTree();
        service.validateTree(nodes);
    }

    @Test(expected = GraphIsNotATreeException.class)
    public void graphHasTwoTreesThrowsException() throws Exception {
        givenGraphWithTwoTrees();
        service.validateTree(nodes);
    }
    @Test
    public void graphHasSingleRoot() throws Exception {
        givenTreeWithSingleNode();
        service.validateTree(nodes);
    }
    @Test(expected = TreeHasCyclesException.class)
    public void graphHasSingleRootWithParentToItself() throws Exception {
        givenTreeWithSingleNodeAndParentItself();
        service.validateTree(nodes);
    }
    @Test(expected = TreeHasCyclesException.class)
    public void graphHasTwoTreesAndCycle() throws Exception {
        givenGraphWithTwoTreesAndCycle();
        service.validateTree(nodes);
    }



    private void givenTreeWithCycle() {
        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();

        node1.setParent(node2);
        node2.setParent(node3);
        node3.setParent(node1);

        node1.setId(NODE_ID_1);
        node2.setId(NODE_ID_2);
        node3.setId(NODE_ID_3);

        nodes = Arrays.asList(node1, node2, node3);
    }

    private void givenCorrectTree() {
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

        nodes = Arrays.asList(node1, node2, node3, node4);
    }

    private void givenGraphWithTwoTrees() {
        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();
        Node node4 = new Node();
        node1.setParent(null);
        node2.setParent(node1);
        node3.setParent(node1);
        node4.setParent(null);
        node1.setId(NODE_ID_1);
        node2.setId(NODE_ID_2);
        node3.setId(NODE_ID_3);
        node4.setId(NODE_ID_4);

        nodes = Arrays.asList(node1, node2, node3, node4);
    }
    private void givenTreeWithSingleNode() {
        Node node1 = new Node();
        node1.setParent(null);
        node1.setId(NODE_ID_1);
        nodes = Collections.singletonList(node1);
    }
    private void givenTreeWithSingleNodeAndParentItself() {
        Node node1 = new Node();
        node1.setParent(node1);
        node1.setId(NODE_ID_1);
        nodes = Collections.singletonList(node1);
    }

    private void givenGraphWithTwoTreesAndCycle() {
        givenTreeWithCycle();
        Node node4 = new Node();
        node4.setParent(null);
        node4.setId(NODE_ID_4);
        nodes = new ArrayList<>(nodes);
        nodes.add(node4);
    }
}