package com.example.pasir_kowalski_radoslaw.service;

import com.example.pasir_kowalski_radoslaw.model.Group;
import com.example.pasir_kowalski_radoslaw.model.Membership;
import com.example.pasir_kowalski_radoslaw.model.Debt;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.model.Transaction;
import com.example.pasir_kowalski_radoslaw.model.TransactionType;
import com.example.pasir_kowalski_radoslaw.repository.GroupRepository;
import com.example.pasir_kowalski_radoslaw.repository.MembershipRepository;
import com.example.pasir_kowalski_radoslaw.repository.DebtRepository;
import com.example.pasir_kowalski_radoslaw.repository.TransactionRepository;
import com.example.pasir_kowalski_radoslaw.dto.GroupTransactionDTO;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;

@Service
public class GroupTransactionService {

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final DebtRepository debtRepository;
    private final TransactionRepository transactionRepository;

    public GroupTransactionService(GroupRepository groupRepository, MembershipRepository membershipRepository, DebtRepository debtRepository, TransactionRepository transactionRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.debtRepository = debtRepository;
        this.transactionRepository = transactionRepository;
    }

    public void addGroupTransaction(GroupTransactionDTO dto, User currentUser) {
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy!"));

        List<Membership> members = membershipRepository.findByGroupId(group.getId());
        List<Long> selectedUserIds = dto.getSelectedUserIds();

        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            throw new IllegalArgumentException("Nie wybrano żadnych użytkowników");
        }

        double amountPerUser = dto.getAmount() / selectedUserIds.size();

        for (Membership member : members) {
            User debtor = member.getUser();
            if (!debtor.getId().equals(currentUser.getId()) && selectedUserIds.contains(debtor.getId())) {
                Debt debt = new Debt();
                debt.setDebtor(debtor);
                debt.setCreditor(currentUser);
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(dto.getTitle());
                debtRepository.save(debt);
            }
        }

        // Create a personal expense transaction for the user who added the group transaction
        Transaction personalExpense = new Transaction();
        personalExpense.setAmount(dto.getAmount());
        personalExpense.setType(TransactionType.EXPENSE);
        personalExpense.setTags("Wydatek grupowy: " + group.getName()); // Or a more descriptive tag
        personalExpense.setNotes(dto.getTitle());
        personalExpense.setUser(currentUser);
        personalExpense.setTimestamp(LocalDateTime.now());
        transactionRepository.save(personalExpense);
    }
} 