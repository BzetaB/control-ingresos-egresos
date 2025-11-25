package com.bzetab.finanzas_personales.repositories;

import com.bzetab.finanzas_personales.models.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
}
