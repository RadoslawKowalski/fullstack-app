package com.example.pasir_kowalski_radoslaw.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.pasir_kowalski_radoslaw.model.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")

public class Transaction {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Double amount;
    @Setter
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Setter
    private String tags;
    @Setter
    private String notes;
    @Setter
    private LocalDateTime timestamp;
    public Transaction(Double amount, TransactionType type, String tags, String notes) {
        this.amount = amount;
        this.type = type;
        this.tags = tags;
        this.notes = notes;
        this.timestamp = LocalDateTime.now();
    }
}
