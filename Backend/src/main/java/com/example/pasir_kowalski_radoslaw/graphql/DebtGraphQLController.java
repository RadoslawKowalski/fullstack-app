package com.example.pasir_kowalski_radoslaw.graphql;

import com.example.pasir_kowalski_radoslaw.model.Debt;
import com.example.pasir_kowalski_radoslaw.model.User;
import com.example.pasir_kowalski_radoslaw.service.DebtService;
import com.example.pasir_kowalski_radoslaw.service.TransactionService;
import com.example.pasir_kowalski_radoslaw.dto.DebtDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DebtGraphQLController {

    private final TransactionService transactionService;
    private final DebtService debtService;

    public DebtGraphQLController(TransactionService transactionService, DebtService debtService) {
        this.transactionService = transactionService;
        this.debtService = debtService;
    }

    @QueryMapping
    public List<Debt> groupDebts(@Argument Long groupId) {
        return debtService.getDebtsByGroupId(groupId);
    }

    @MutationMapping
    public Boolean deleteDebt(@Argument Long debtId) {
        User currentUser = transactionService.getCurrentUser();
        debtService.deleteDebt(debtId, currentUser);
        return true;
    }

    @MutationMapping
    public Boolean markDebtAsPaid(@Argument Long debtId) {
        User currentUser = transactionService.getCurrentUser();
        return debtService.markAsPaid(debtId, currentUser);
    }

    @MutationMapping
    public Boolean confirmDebtPayment(@Argument Long debtId) {
        User currentUser = transactionService.getCurrentUser();
        return debtService.confirmPayment(debtId, currentUser);
    }

    @MutationMapping
    public Debt createDebt(@Argument DebtDTO debtDTO) {
        return debtService.createDebt(debtDTO);
    }
} 