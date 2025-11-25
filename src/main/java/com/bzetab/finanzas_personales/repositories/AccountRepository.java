package com.bzetab.finanzas_personales.repositories;

import com.bzetab.finanzas_personales.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
