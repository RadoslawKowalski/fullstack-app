package com.example.pasir_kowalski_radoslaw.dto;

import lombok.Getter;

@Getter
public class BalanceDto {
    private final double totalIncome;
    private final double totalExpense;
    private final double balance;

    public BalanceDto(double totalIncome, double totalExpense) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = totalIncome - totalExpense;
    }

}