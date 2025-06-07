package com.example.pasir_kowalski_radoslaw.controller;

import com.example.pasir_kowalski_radoslaw.model.Group;
import com.example.pasir_kowalski_radoslaw.service.GroupService;
import com.example.pasir_kowalski_radoslaw.dto.GroupDTO;
import com.example.pasir_kowalski_radoslaw.dto.GroupResponseDTO;
import com.example.pasir_kowalski_radoslaw.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class GroupGraphQLController {

    private final GroupService groupService;

    public GroupGraphQLController(GroupService groupService) {
        this.groupService = groupService;
    }

    @QueryMapping
    public List<Group> groups() {
        return groupService.getAllGroups();
    }

    @MutationMapping
    public Group createGroup(@Valid @Argument GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument Long id) {
        groupService.deleteGroup(id);
        return true;
    }

    @SchemaMapping
    public Long ownerId(GroupResponseDTO groupResponseDTO) {
        return groupResponseDTO.getOwnerId();
    }
} 