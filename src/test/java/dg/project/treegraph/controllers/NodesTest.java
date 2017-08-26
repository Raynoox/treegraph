package dg.project.treegraph.controllers;

import dg.project.treegraph.TreeGraphApplication;
import dg.project.treegraph.dto.NodeDTO;
import dg.project.treegraph.models.Node;
import dg.project.treegraph.services.NodeService;
import dg.project.treegraph.services.exceptions.GraphIsNotATreeException;
import dg.project.treegraph.services.exceptions.TreeHasCyclesException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = TreeGraphApplication.class)
public class NodesTest {
    private static final String NODES_ENDPOINT = "/api/nodes/";
    private static final Long NODE_ID_1 = 1L;
    private static final Long NODE_PARENT_ID_3 = NODE_ID_1;
    private static final Long NODE_PARENT_ID_2 = NODE_ID_1;
    private static final Long NODE_VALUE_1 = 1L;

    private static final Long NODE_ID_2 = 2L;
    private static final Long NODE_PARENT_ID_4 = NODE_ID_2;
    private static final Long NODE_VALUE_2 = 2L;

    private static final Long NODE_ID_3 = 3L;
    private static final Long NODE_VALUE_3 = 3L;

    private static final Long NODE_ID_4 = 4L;
    private static final Long NODE_VALUE_4 = 4L;
    private static final Long NODE_PARENT_ID_1 = null;

    private static final Long NOT_USED_ID = 123L;

    @MockBean
    private NodeService nodeService;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGet() throws Exception {
        when(nodeService.getAll()).thenReturn(exampleListOfFourNodes());

        mockMvc.perform((get(NODES_ENDPOINT)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(NODE_ID_1))
                .andExpect(jsonPath("$[0].parentId").value(nullValue()))
                .andExpect(jsonPath("$[0].value").value(NODE_VALUE_1))
                .andExpect(jsonPath("$[1].id").value(NODE_ID_2))
                .andExpect(jsonPath("$[1].parentId").value(NODE_PARENT_ID_2))
                .andExpect(jsonPath("$[1].value").value(NODE_VALUE_2))
                .andExpect(jsonPath("$[2].id").value(NODE_ID_3))
                .andExpect(jsonPath("$[2].parentId").value(NODE_PARENT_ID_3))
                .andExpect(jsonPath("$[2].value").value(NODE_VALUE_3))
                .andExpect(jsonPath("$[3].id").value(NODE_ID_4))
                .andExpect(jsonPath("$[3].parentId").value(NODE_PARENT_ID_4))
                .andExpect(jsonPath("$[3].value").value(NODE_VALUE_4))
        ;
        verify(nodeService, times(1)).getAll();
    }

    @Test
    public void testEmptyGet() throws Exception {
        when(nodeService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform((get(NODES_ENDPOINT)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(0)));
        verify(nodeService, times(1)).getAll();
    }
    @Test
    public void testGetById() throws Exception {
        when(nodeService.getById(NODE_ID_2)).thenReturn(Optional.ofNullable(singleNode(NODE_ID_2, NODE_PARENT_ID_2, NODE_VALUE_2)));

        mockMvc.perform((get(NODES_ENDPOINT +NODE_ID_2)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(NODE_ID_2))
                .andExpect(jsonPath("$.parentId").value(NODE_PARENT_ID_2))
                .andExpect(jsonPath("$.value").value(NODE_VALUE_2));

        verify(nodeService, times(1)).getById(NODE_ID_2);
    }

    @Test
    public void testGetByIdNoContent() throws Exception {
        when(nodeService.getById(NOT_USED_ID)).thenReturn(Optional.empty());

        mockMvc.perform((get(NODES_ENDPOINT+NOT_USED_ID)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(nodeService, times(1)).getById(NOT_USED_ID);
    }
    @Test
    public void testAdd() throws Exception {
        NodeDTO dto = singleNode(null, NODE_PARENT_ID_2, NODE_VALUE_2);
        performPostAction(dto).andExpect(status().isOk());
        verify(nodeService, times(1)).add(dto);
    }

    @Test
    public void testNullValueAdd() throws Exception {
        NodeDTO dto = singleNode(null, NODE_PARENT_ID_2, null);
        performPostAction(dto).andExpect(status().isOk());
        verify(nodeService, times(1)).add(dto);
    }
    @Test
    public void testNullParentAdd() throws Exception {
        NodeDTO dto = singleNode(null, null, NODE_VALUE_2);
        performPostAction(dto).andExpect(status().isOk());
        verify(nodeService, times(1)).add(dto);
    }
    @Test
    public void testAddButTreeNotValidated() throws Exception {
        NodeDTO dto = singleNode(null, NODE_PARENT_ID_1, NODE_VALUE_1);
        doThrow(GraphIsNotATreeException.class).when(nodeService).add(dto);
        performPostAction(dto).andExpect(status().isUnprocessableEntity());
        verify(nodeService, times(1)).add(dto);

        dto = singleNode(null, NODE_PARENT_ID_2, NODE_VALUE_2);
        doThrow(TreeHasCyclesException.class).when(nodeService).add(dto);
        performPostAction(dto).andExpect(status().isUnprocessableEntity());
        verify(nodeService, times(1)).add(dto);
    }
    @Test
    public void testAddButDataException() throws Exception {
        NodeDTO dto = singleNode(null, NODE_PARENT_ID_2, NODE_VALUE_2);
        doThrow(DataIntegrityViolationException.class).when(nodeService).add(dto);
        performPostAction(dto).andExpect(status().isBadRequest());
        verify(nodeService, times(1)).add(dto);
    }
    @Test
    public void testNullParentAndNullValueAdd() throws Exception {
        NodeDTO dto = singleNode(null, null, null);
        performPostAction(dto).andExpect(status().isOk());
        verify(nodeService, times(1)).add(dto);
    }
    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(NODES_ENDPOINT+NODE_ID_2)).andExpect(status().isOk());
        verify(nodeService, times(1)).remove(NODE_ID_2);
    }

    @Test
    public void testUpdate() throws Exception {
        NodeDTO dto = singleNode(NODE_ID_2, NODE_PARENT_ID_2, NODE_VALUE_2);
        performPatchAction(dto)
                .andExpect(status().isOk());
        verify(nodeService, times(1)).update(dto);

    }
    @Test
    public void testNullParentUpdate() throws Exception {
        NodeDTO dto = singleNode(NODE_ID_2, null, NODE_VALUE_2);

        performPatchAction(dto)
                .andExpect(status().isOk());

        verify(nodeService, times(1)).update(dto);
    }
    @Test
    public void testNullValueUpdate() throws Exception {
        NodeDTO dto = singleNode(NODE_ID_2, NODE_PARENT_ID_2, null);
        performPatchAction(dto)
                .andExpect(status().isOk());
        verify(nodeService, times(1)).update(dto);
    }
    @Test
    public void testNullValueAndNullParentUpdate() throws Exception {
        NodeDTO dto = singleNode(NODE_ID_2, null, null);
        performPatchAction(dto)
                .andExpect(status().isOk());
        verify(nodeService, times(1)).update(dto);
    }
    @Test
    public void testUpdateThrowsGraphException() throws Exception{
        NodeDTO dto = singleNode(NODE_ID_2, NODE_PARENT_ID_2, NODE_VALUE_2);
        doThrow(GraphIsNotATreeException.class).when(nodeService).update(dto);
        performPatchAction(dto)
                .andExpect(status().isUnprocessableEntity());
        verify(nodeService, times(1)).update(dto);
    }
    @Test
    public void testUpdateThrowsTreeException() throws Exception{
        NodeDTO dto = singleNode(NODE_ID_2, NODE_PARENT_ID_2, NODE_VALUE_2);
        doThrow(TreeHasCyclesException.class).when(nodeService).update(dto);
        performPatchAction(dto)
                .andExpect(status().isUnprocessableEntity());
        verify(nodeService, times(1)).update(dto);
    }
    @Test
    public void testUpdateThrowsDataIntegrityException() throws Exception {
        NodeDTO dto = singleNode(NODE_ID_2, NODE_PARENT_ID_2, NODE_VALUE_2);
        doThrow(DataIntegrityViolationException.class).when(nodeService).update(dto);
        performPatchAction(dto)
                .andExpect(status().isBadRequest());
        verify(nodeService, times(1)).update(dto);
    }
    private ResultActions performPatchAction(NodeDTO dto) throws Exception{
        return mockMvc.perform(patch(NODES_ENDPOINT+dto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"parentId\":\""+dto.getParentId()+"\",\"value\":\""+dto.getValue()+"\"}"));
    }

    private ResultActions performPostAction(NodeDTO dto) throws Exception {
        return mockMvc.perform(post(NODES_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"parentId\":\""+dto.getParentId()+"\",\"value\":\""+dto.getValue()+"\"}"));
    }
    private List<NodeDTO> exampleListOfFourNodes() {
        Node node1 = new Node.Builder().value(NODE_VALUE_1).parentId(NODE_PARENT_ID_1).build();
        node1.setId(NODE_ID_1);
        Node node2 = new Node.Builder().value(NODE_VALUE_2).parentId(NODE_PARENT_ID_2).build();
        node2.setId(NODE_ID_2);
        Node node3 = new Node.Builder().value(NODE_VALUE_3).parentId(NODE_PARENT_ID_3).build();
        node3.setId(NODE_ID_3);
        Node node4 = new Node.Builder().value(NODE_VALUE_4).parentId(NODE_PARENT_ID_4).build();
        node4.setId(NODE_ID_4);
        return Stream.of(node1, node2, node3, node4).map(NodeDTO::new).collect(Collectors.toList());
    }

    private NodeDTO singleNode(Long id, Long parentId, Long value) {
        NodeDTO result = new NodeDTO();
        result.setId(id);
        result.setParentId(parentId);
        result.setValue(value);
        return result;
    }
}