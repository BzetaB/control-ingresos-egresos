package com.bzetab.finanzas_personales.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Double amount;
    private String description;
    @UpdateTimestamp
    private LocalDate date;

    //Relationship with ExpenseCategory
    @ManyToOne
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategory expenseCategory;

    //Relationship with Account
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
