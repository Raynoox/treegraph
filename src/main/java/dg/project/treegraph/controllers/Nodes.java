package dg.project.treegraph.controllers;

import dg.project.treegraph.dto.NodeDTO;
import dg.project.treegraph.services.NodeService;
import dg.project.treegraph.services.exceptions.TreeValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/api/nodes")
public class Nodes {
    private final NodeService nodeService;

    @Autowired
    public Nodes(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public List<NodeDTO> get() {
        return nodeService.getAll();
    }
    @GetMapping(value = "{id}")
    public NodeDTO getById(@PathVariable("id") Long id, HttpServletResponse response) {
        Optional<NodeDTO> result = nodeService.getById(id);
        if(!result.isPresent()) {
            response.setStatus(NO_CONTENT.value());
        }
        return result.orElse(null);
    }
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void add(@RequestBody NodeDTO dto, HttpServletResponse response) {
        try {
            nodeService.add(dto);
        } catch(DataIntegrityViolationException e) {
            response.setStatus(BAD_REQUEST.value());
        } catch (TreeValidationException e) {
            response.setStatus(UNPROCESSABLE_ENTITY.value());
        }
    }
    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        try {
            nodeService.remove(id);
        } catch(DataIntegrityViolationException e) {
            response.sendError(BAD_REQUEST.value(),e.getMessage());
        } catch(TreeValidationException e) {
            response.sendError(UNPROCESSABLE_ENTITY.value(),e.getMessage());
        }
    }
    @PatchMapping(value = "{id}")
    public void update(@PathVariable("id") Long id,@RequestBody NodeDTO dto, HttpServletResponse response) throws IOException {
        dto.setId(id);
        try{
            nodeService.update(dto);
        } catch(DataIntegrityViolationException e) {
            response.sendError(BAD_REQUEST.value(), e.getMessage());
        } catch (TreeValidationException e) {
            response.sendError(UNPROCESSABLE_ENTITY.value(), e.getMessage());
        }
    }

}
