package com.example.pasir_kowalski_radoslaw.service;

import com.example.pasir_kowalski_radoslaw.dto.TransactionDTO;
import com.example.pasir_kowalski_radoslaw.dto.BalanceDto;
import com.example.pasir_kowalski_radoslaw.model.Transaction;
import com.example.pasir_kowalski_radoslaw.model.TransactionType;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.repository.TransactionRepository;
import com.example.pasir_kowalski_radoslaw.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private static final String TRANSACTION_NOT_FOUND_MESSAGE = "Nie znaleziono transakcji o ID ";
    private static final String LOGGED_IN_USER_NOT_FOUND_MESSAGE = "Nie znaleziono zalogowanego użytkownika";

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(LOGGED_IN_USER_NOT_FOUND_MESSAGE));
    }

    public List<Transaction> getAllTransactions() {
        User user = getCurrentUser();
        return transactionRepository.findAllByUser(user);
    }
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND_MESSAGE + id));
    }
    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND_MESSAGE + id));

        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new SecurityException("Brak dostępu do transakcji");
        }

        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(String.valueOf(transactionDTO.getType())));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());

        return transactionRepository.save(transaction);
    }
    public Transaction createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(String.valueOf(transactionDTO.getType())));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());
        transaction.setUser(getCurrentUser());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TRANSACTION_NOT_FOUND_MESSAGE + id));
        transactionRepository.deleteById(id);
    }

    public BalanceDto getUserBalance(User user, Float days) {
        List<Transaction> userTransactions = transactionRepository.findAllByUser(user);

        if (days != null) {
            LocalDateTime since = LocalDateTime.now().minusDays(days.longValue());
            userTransactions = userTransactions.stream()
                .filter(t -> t.getTimestamp() != null && t.getTimestamp().isAfter(since))
                .toList();
        }

        double totalIncome = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();

        double totalExpense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();

        return new BalanceDto(totalIncome, totalExpense);
    }
}
