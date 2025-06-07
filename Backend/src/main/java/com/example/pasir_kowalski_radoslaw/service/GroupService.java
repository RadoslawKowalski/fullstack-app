package com.example.pasir_kowalski_radoslaw.service;

import com.example.pasir_kowalski_radoslaw.model.Group;
import com.example.pasir_kowalski_radoslaw.model.Membership;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.repository.GroupRepository;
import com.example.pasir_kowalski_radoslaw.repository.MembershipRepository;
import com.example.pasir_kowalski_radoslaw.repository.DebtRepository;
import com.example.pasir_kowalski_radoslaw.dto.GroupDTO;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final TransactionService transactionService;

    public GroupService(GroupRepository groupRepository, MembershipRepository membershipRepository, DebtRepository debtRepository, TransactionService transactionService) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.transactionService = transactionService;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group createGroup(GroupDTO groupDTO) {
        User owner = transactionService.getCurrentUser(); // Get the current logged-in user

        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setOwner(owner);

        Group savedGroup = groupRepository.save(group);

        Membership membership = new Membership();
        membership.setUser(owner);
        membership.setGroup(savedGroup);
        membershipRepository.save(membership);

        return savedGroup;
    }

    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupa o ID " + id + " nie istnieje."));

        // Usuń powiązania
        debtRepository.deleteAll(debtRepository.findByGroupId(id));
        membershipRepository.deleteAll(membershipRepository.findByGroupId(id));

        // Usuń grupę
        groupRepository.delete(group);
    }
} 