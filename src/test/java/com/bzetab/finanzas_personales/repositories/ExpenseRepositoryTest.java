package com.bzetab.finanzas_personales.repositories;

import com.bzetab.finanzas_personales.models.Account;
import com.bzetab.finanzas_personales.models.Expense;
import com.bzetab.finanzas_personales.models.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestEntityManager entityManager;

    private ExpenseCategory expenseCategoryTest;
    private Account accountTest;
    private Expense expenseTest;

    @BeforeEach
    void setUp(){
        expenseCategoryTest = expenseCategoryRepository.save(
                ExpenseCategory.builder().name("Estudios").build()
        );

        accountTest = accountRepository.save(
                Account.builder()
                        .name("Cuenta de Ahorros")
                        .bank("Banco XYZ")
                        .balance(1000.0)
                        .build()
        );

        expenseTest = Expense.builder()
                .amount(500.00)
                .description("Mensualidad")
                .expenseCategory(expenseCategoryTest)
                .account(accountTest)
                .date(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Debería guardar los valores del gasto junto con sus relaciones Categoría y Cuenta")
    void shouldPersistExpenseWithRelationships() {
        Expense savedExpense = expenseRepository.save(expenseTest);
        entityManager.flush();
        entityManager.clear();

        Expense dbExpense = expenseRepository.findById(savedExpense.getId()).orElseThrow();

        assertEquals("Estudios", dbExpense.getExpenseCategory().getName());
        assertEquals(500.00, dbExpense.getAmount());
        assertEquals("Mensualidad", dbExpense.getDescription());
        assertEquals("Cuenta de Ahorros", dbExpense.getAccount().getName());
    }

    @Test
    @DisplayName("No debería permitir guardar un gasto sin Categoría")
    void shouldFailWhenCategoryIsNull(){
        Expense expenseCategoryNull = Expense.builder()
                .amount(100.00)
                .description("Gasto sin categoría")
                .account(accountTest)
                .build();

        assertThrows(Exception.class, () -> {
            expenseRepository.saveAndFlush(expenseCategoryNull);
        });
    }

    @Test
    @DisplayName("No debería permitir guardar un gasto sin Cuenta")
    void shouldFailWhenAccountIsNull(){
        Expense expenseAccountNull = Expense.builder()
                .expenseCategory(expenseCategoryTest)
                .amount(100.00)
                .description("Gasto sin cuenta")
                .build();

        assertThrows(Exception.class, () -> {
            expenseRepository.saveAndFlush(expenseAccountNull);
        });
    }

    @Test
    @DisplayName("No debería permitir guardar expense sin amount")
    void shouldFailWhenAmountNull() {
        Expense invalid = Expense.builder()
                .description("Sin amount")
                .expenseCategory(expenseCategoryTest)
                .account(accountTest)
                .build();

        assertThrows(Exception.class, () -> {
            expenseRepository.saveAndFlush(invalid);
        });
    }

    @Test
    @DisplayName("No debería eliminar una categoría si tiene expenses asociados")
    void shouldNotDeleteCategoryIfExpensesExist() {
        Expense savedExpense = expenseRepository.save(expenseTest);

        assertThrows(Exception.class, () -> {
            expenseCategoryRepository.delete(expenseCategoryTest);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Debería actualizar la Categoría del gasto correctamente")
    void shouldUpdateExpenseCategory() {
        Expense savedExpense = expenseRepository.save(expenseTest);
        ExpenseCategory newCategory = expenseCategoryRepository.save(
                ExpenseCategory.builder().name("Universidad").build());

        savedExpense.setExpenseCategory(newCategory);
        expenseRepository.save(savedExpense);
        entityManager.flush();
        entityManager.clear();

        Expense updatedExpense = expenseRepository.findById(savedExpense.getId()).orElseThrow();

        assertEquals("Universidad",updatedExpense.getExpenseCategory().getName());
        assertTrue(expenseCategoryRepository.findById(newCategory.getId()).isPresent());
    }

    @Test
    @DisplayName("Debería actualizar la Cuenta del gasto correctamente")
    void shouldUpdateExpenseAccount() {
        Expense savedExpense = expenseRepository.save(expenseTest);
        Account newAccount = accountRepository.save(
                Account.builder().name("Tarjeta de Credito").balance(900.00).build());

        savedExpense.setAccount(newAccount);
        expenseRepository.save(savedExpense);
        entityManager.flush();
        entityManager.clear();

        Expense updatedExpense = expenseRepository.findById(savedExpense.getId()).orElseThrow();

        assertTrue(expenseRepository.findById(updatedExpense.getId()).isPresent());
        assertEquals("Tarjeta de Credito",updatedExpense.getAccount().getName());
        assertTrue(accountRepository.findById(newAccount.getId()).isPresent());
    }

    @Test
    @DisplayName("Debería eliminar el gasto, pero no la Categoría y la Cuenta de la base de datos")
    void shouldDeleteButKeepCategoryAndAccount() {
        Expense savedExpense = expenseRepository.save(expenseTest);
        entityManager.flush();

        expenseRepository.delete(savedExpense);
        entityManager.flush();
        entityManager.clear();

        assertFalse(expenseRepository.findById(savedExpense.getId()).isPresent());

        assertTrue(expenseCategoryRepository.findById(expenseCategoryTest.getId()).isPresent());
        assertTrue(accountRepository.findById(accountTest.getId()).isPresent());
    }
}
