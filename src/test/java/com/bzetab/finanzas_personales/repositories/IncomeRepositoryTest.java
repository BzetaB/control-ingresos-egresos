package com.bzetab.finanzas_personales.repositories;

import com.bzetab.finanzas_personales.models.Account;
import com.bzetab.finanzas_personales.models.Income;
import com.bzetab.finanzas_personales.models.IncomeSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private IncomeSourceRepository incomeSourceRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestEntityManager entityManager;

    private IncomeSource incomeSourceTest;
    private Account accountTest;
    private Income incomeTest;

    @BeforeEach
    void setUp(){
        incomeSourceTest = incomeSourceRepository.save(
                IncomeSource.builder().name("Salario").build()
        );

        accountTest = accountRepository.save(
                Account.builder()
                        .name("Cuenta de Ahorros")
                        .bank("Banco XYZ")
                        .balance(1000.0)
                        .build()
        );

        incomeTest = Income.builder()
                .amount(2500.00)
                .incomeSource(incomeSourceTest)
                .account(accountTest)
                .build();
    }

    @Test
    @DisplayName("Debería guardar los valores del ingreso junto con sus relaciones Fuente de Ingreso y Cuenta")
    void shouldPersistIncomeWithRelationships() {
        Income savedIncome = incomeRepository.save(incomeTest);
        entityManager.flush();
        entityManager.clear();

        Income dbIncome = incomeRepository.findById(savedIncome.getId()).orElseThrow();

        assertNotNull(savedIncome.getId());
        assertEquals("Salario", dbIncome.getIncomeSource().getName());
        assertEquals(2500.00, dbIncome.getAmount());
        assertEquals("Cuenta de Ahorros", dbIncome.getAccount().getName());
    }

    @Test
    @DisplayName("No debería permitir guardar un ingreso sin Fuente de Ingreso")
    void shouldFailWhenIncomeSourceIsNull() {
        Income incomeSourceNull = Income.builder()
                .amount(2000.00)
                .account(accountTest)
                .date(LocalDate.now())
                .build();

        assertThrows(Exception.class, () -> incomeRepository.saveAndFlush(incomeSourceNull));
    }

    @Test
    @DisplayName("No debería permitir guardar un ingreso sin Cuenta")
    void shouldFailWhenAccountIsNull() {
        Income incomeAccountNull = Income.builder()
                .incomeSource(incomeSourceTest)
                .amount(2000.00)
                .date(LocalDate.now())
                .build();

        assertThrows(Exception.class, () -> incomeRepository.saveAndFlush(incomeAccountNull));
    }

    @Test
    @DisplayName("No debería permitir guardar un ingreso sin amount")
    void shouldFailWhenAmountNull() {
        Income invalid = Income.builder()
                .incomeSource(incomeSourceTest)
                .account(accountTest)
                .date(LocalDate.now())
                .build();

        assertThrows(Exception.class, () -> incomeRepository.saveAndFlush(invalid));
    }

    @Test
    @DisplayName("Debería actualizar la Fuente de Ingreso correctamente")
    void shouldUpdateIncomeSource() {
        Income savedIncome = incomeRepository.save(incomeTest);

        IncomeSource newSource = incomeSourceRepository.save(
                IncomeSource.builder().name("Bonificación").build()
        );

        savedIncome.setIncomeSource(newSource);
        incomeRepository.save(savedIncome);
        entityManager.flush();
        entityManager.clear();

        Income updatedIncome = incomeRepository.findById(savedIncome.getId()).orElseThrow();

        assertEquals("Bonificación", updatedIncome.getIncomeSource().getName());
        assertTrue(incomeSourceRepository.findById(newSource.getId()).isPresent());
    }

    @Test
    @DisplayName("Debería actualizar la Cuenta del ingreso correctamente")
    void shouldUpdateIncomeAccount() {
        Income savedIncome = incomeRepository.save(incomeTest);

        Account newAccount = accountRepository.save(
                Account.builder().name("Tarjeta de Crédito").balance(5000.00).build()
        );

        savedIncome.setAccount(newAccount);
        incomeRepository.save(savedIncome);
        entityManager.flush();
        entityManager.clear();

        Income updatedIncome = incomeRepository.findById(savedIncome.getId()).orElseThrow();

        assertEquals("Tarjeta de Crédito", updatedIncome.getAccount().getName());
        assertTrue(accountRepository.findById(newAccount.getId()).isPresent());
    }

    @Test
    @DisplayName("Debería eliminar el ingreso, pero no la Fuente de Ingreso ni la Cuenta")
    void shouldDeleteButKeepIncomeSourceAndAccount() {
        Income savedIncome = incomeRepository.save(incomeTest);
        entityManager.flush();

        incomeRepository.delete(savedIncome);
        entityManager.flush();
        entityManager.clear();

        assertFalse(incomeRepository.findById(savedIncome.getId()).isPresent());
        assertTrue(incomeSourceRepository.findById(incomeSourceTest.getId()).isPresent());
        assertTrue(accountRepository.findById(accountTest.getId()).isPresent());
    }
}
