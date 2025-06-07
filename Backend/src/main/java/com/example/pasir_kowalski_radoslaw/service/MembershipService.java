package com.example.pasir_kowalski_radoslaw.service;

import com.example.pasir_kowalski_radoslaw.model.Membership;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.model.Group;
import com.example.pasir_kowalski_radoslaw.repository.MembershipRepository;
import com.example.pasir_kowalski_radoslaw.repository.GroupRepository;
import com.example.pasir_kowalski_radoslaw.repository.UserRepository;
import com.example.pasir_kowalski_radoslaw.dto.MembershipDTO;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public MembershipService(MembershipRepository membershipRepository, GroupRepository groupRepository, UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public List<Membership> getGroupMembers(Long groupId) {
        return membershipRepository.findByGroupId(groupId);
    }

    public Membership addMember(MembershipDTO membershipDTO) {
        User user = userRepository.findByEmail(membershipDTO.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o emailu: " + membershipDTO.getUserEmail()));

        Group group = groupRepository.findById(membershipDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + membershipDTO.getGroupId()));

        // WALIDACJA: sprawdź czy użytkownik już jest członkiem tej grupy
        boolean alreadyMember = membershipRepository.findByGroupId(group.getId()).stream()
            .anyMatch(membership -> membership.getUser().getId().equals(user.getId()));

        if (alreadyMember) {
            throw new IllegalStateException("Użytkownik jest już członkiem tej grupy.");
        }

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setGroup(group);

        return membershipRepository.save(membership);
    }

    public void removeMember(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new EntityNotFoundException("Członkostwo nie istnieje"));

        User currentUser = getCurrentUser(); // kto próbuje usunąć
        User groupOwner = membership.getGroup().getOwner(); // kto jest właścicielem grupy

        if (!currentUser.getId().equals(groupOwner.getId())) {
            throw new SecurityException("Tylko właściciel grupy może usuwać członków.");
        }

        membershipRepository.deleteById(membershipId);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika: " + email));
    }
} 