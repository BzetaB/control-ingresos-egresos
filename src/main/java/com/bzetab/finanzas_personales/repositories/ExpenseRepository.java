package com.bzetab.finanzas_personales.repositories;

import com.bzetab.finanzas_personales.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
