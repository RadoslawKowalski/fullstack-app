package com.example.pasir_kowalski_radoslaw.controller;

import com.example.pasir_kowalski_radoslaw.model.Transaction;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.service.TransactionService;
import com.example.pasir_kowalski_radoslaw.dto.TransactionDTO;
import com.example.pasir_kowalski_radoslaw.dto.BalanceDto;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    public TransactionGraphQLController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @MutationMapping
    public Transaction addTransaction(@Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @MutationMapping
    public Transaction updateTransaction(
            @Argument Long id,
            @Argument TransactionDTO transactionDTO
    ) {
        return transactionService.updateTransaction(id, transactionDTO);
    }

    @QueryMapping
    public BalanceDto userBalance(@Argument Float days) {
        User user = transactionService.getCurrentUser();
        return transactionService.getUserBalance(user, days);
    }

    @MutationMapping
    public Boolean deleteTransaction(@Argument Long id) {
        transactionService.deleteTransaction(id);
        return true;
    }
}
