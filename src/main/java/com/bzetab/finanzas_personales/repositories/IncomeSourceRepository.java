package com.bzetab.finanzas_personales.repositories;

import com.bzetab.finanzas_personales.models.IncomeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Long> {
}
