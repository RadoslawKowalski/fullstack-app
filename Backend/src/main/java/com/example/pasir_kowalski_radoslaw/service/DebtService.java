package com.example.pasir_kowalski_radoslaw.service;

import com.example.pasir_kowalski_radoslaw.model.Debt;
import com.example.pasir_kowalski_radoslaw.model.Group;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.repository.DebtRepository;
import com.example.pasir_kowalski_radoslaw.repository.GroupRepository;
import com.example.pasir_kowalski_radoslaw.repository.UserRepository;
import com.example.pasir_kowalski_radoslaw.dto.DebtDTO;

import com.example.pasir_kowalski_radoslaw.model.Transaction;
import com.example.pasir_kowalski_radoslaw.model.TransactionType;
import com.example.pasir_kowalski_radoslaw.repository.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class DebtService {

    private final DebtRepository debtRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DebtService(DebtRepository debtRepository, GroupRepository groupRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.debtRepository = debtRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Debt> getDebtsByGroupId(Long groupId) {
         return debtRepository.findByGroupId(groupId);
    }

    public Debt createDebt(DebtDTO debtDTO) {
        Group group = groupRepository.findById(debtDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono grupy o ID: " + debtDTO.getGroupId()));

        User debtor = userRepository.findById(debtDTO.getDebtorId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono dłużnika o ID: " + debtDTO.getDebtorId()));

        User creditor = userRepository.findById(debtDTO.getCreditorId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono wierzyciela o ID: " + debtDTO.getCreditorId()));

        Debt debt = new Debt();
        debt.setGroup(group);
        debt.setDebtor(debtor);
        debt.setCreditor(creditor);
        debt.setAmount(debtDTO.getAmount());
        debt.setTitle(debtDTO.getTitle()); // tutaj!

        return debtRepository.save(debt);
    }

    public void deleteDebt(Long debtId, User currentUser) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Dług o ID " + debtId + " nie istnieje."));

        if (!debt.getCreditor().getId().equals(currentUser.getId())) {
            throw new SecurityException("Tylko wierzyciel może usunąć ten dług.");
        }

        debtRepository.delete(debt);
    }

    public boolean markAsPaid(Long debtId, User user) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono długu"));

        if (!debt.getDebtor().getId().equals(user.getId())) {
            throw new SecurityException("Nie jesteś dłużnikiem");
        }

        debt.setMarkedAsPaid(true);
        debtRepository.save(debt);
        return true;
    }

    public boolean confirmPayment(Long debtId, User user) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono długu"));

        if (!debt.getCreditor().getId().equals(user.getId())) {
            throw new SecurityException("Nie jesteś wierzycielem");
        }

        if (!debt.isMarkedAsPaid()) {
            throw new IllegalStateException("Dłużnik jeszcze nie oznaczył jako opłacone");
        }

        debt.setConfirmedByCreditor(true);
        debtRepository.save(debt);

        // Dodanie transakcji dla wierzyciela (przychód)
        Transaction incomeTx = new Transaction();
        incomeTx.setAmount(debt.getAmount());
        incomeTx.setType(TransactionType.INCOME);
        incomeTx.setTags("Spłata długu");
        incomeTx.setNotes("Spłata długu od: " + debt.getDebtor().getEmail());
        incomeTx.setUser(debt.getCreditor());
        transactionRepository.save(incomeTx);

        // Dodanie transakcji dla dłużnika (wydatek)
        Transaction expenseTx = new Transaction();
        expenseTx.setAmount(debt.getAmount());
        expenseTx.setType(TransactionType.EXPENSE);
        expenseTx.setTags("Spłata długu");
        expenseTx.setNotes("Spłacono dług dla: " + debt.getCreditor().getEmail());
        expenseTx.setUser(debt.getDebtor());
        transactionRepository.save(expenseTx);

        return true;
    }
} 