package pl.edu.agh.iet.katabank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Deposit implements BankProduct {

    private BigDecimal balance;
    private Account connectedAccount;
    private final UUID id;
    private final String INCORRECT_AMOUNT_MESSAGE = "Incorrect initial balance to open deposit: ";
    private final LocalDate openDate;
    private final int durationInMonths;

    public Deposit (Account account, BigDecimal initialBalance) {
        try {
            account.withdraw(initialBalance);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INCORRECT_AMOUNT_MESSAGE + initialBalance, e);
        }

        this.balance = initialBalance;
        this.connectedAccount = account;
        this.id = UUID.randomUUID();
        this.openDate = LocalDate.now();
        this.durationInMonths = 12;
    }

    public Deposit(Account account, BigDecimal initialBalance, LocalDate openDate, int durationInMonths) {
        try {
            account.withdraw(initialBalance);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INCORRECT_AMOUNT_MESSAGE + initialBalance, e);
        }

        this.balance = initialBalance;
        this.connectedAccount = account;
        this.id = UUID.randomUUID();
        this.openDate = openDate;
        this.durationInMonths = durationInMonths;
    }

    public Account getConnectedAccount() {
        return this.connectedAccount;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    @Override
    public Customer getOwner() {
        return this.connectedAccount.getOwner();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deposit deposit = (Deposit) o;
        return Objects.equals(id, deposit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void finishDeposit(LocalDate date) {
        if(openDate.plusMonths(durationInMonths).isBefore(date)){
           throw new RuntimeException("Cannot close deposit on date: " + date);
        }
        BigDecimal closeBalance = this.balance;
        this.balance = BigDecimal.ZERO;
        this.connectedAccount.deposit(closeBalance);
    }
}