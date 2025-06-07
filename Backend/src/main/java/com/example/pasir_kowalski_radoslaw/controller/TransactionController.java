package com.example.pasir_kowalski_radoslaw.controller;


import com.example.pasir_kowalski_radoslaw.dto.TransactionDTO;
import com.example.pasir_kowalski_radoslaw.model.Transaction;
import com.example.pasir_kowalski_radoslaw.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDetails) {
        Transaction transaction = transactionService.getTransactionById(id);
                //.orElseThrow(()->new RuntimeException("Transaction not found with id " + id));
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setType(transactionDetails.getType());
        transaction.setTags(transactionDetails.getTags());
        transaction.setNotes(transactionDetails.getNotes());
        Transaction updatedTransaction = transactionService.UpdateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDTO transaction) {
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(savedTransaction);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
                //.orElseThrow(() -> new RuntimeException("Transaction not found with id " + id));

        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
