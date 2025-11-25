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
@Table(name = "income")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Double amount;
    @UpdateTimestamp
    private LocalDate date;

    //Relationship with IncomeSource
    @ManyToOne
    @JoinColumn(name = "income_source_id", nullable = false)
    private IncomeSource incomeSource;

    //Relationship with Account
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
