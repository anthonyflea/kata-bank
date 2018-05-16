package pl.edu.agh.iet.katabank

import spock.lang.Specification

import java.time.LocalDate

import static org.assertj.core.api.Assertions.assertThat

class DepositTest extends Specification {

    private static final String ERROR_MESSAGE_OPEN_DEPOSIT = 'Incorrect initial balance to open deposit: '
    private static final String ERROR_MESSAGE_CLOSE_DEPOSIT = 'Cannot close deposit on date: '

    private final Customer customer = new Customer()
    private final Account account = new Account(customer)
    private Deposit deposit
    private BigDecimal amount
    private LocalDate openDate
    private int durationInMonths

    def "two deposits created for the same account are not equal"() {
        when:
        amount = 50
        account.setBalance(200)
        def firstDeposit = new Deposit(account, amount)
        def secondDeposit = new Deposit(account, amount)

        then:
        !firstDeposit.equals(secondDeposit)
    }

    def "try to open a deposit with a negative balance"() {
        when:
        amount = -1
        account.setBalance(100)
        def deposit = new Deposit(account, amount)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == ERROR_MESSAGE_OPEN_DEPOSIT + amount
    }

    def "try to open a deposit with a zero balance"() {
        when:
        amount = 0
        account.setBalance(100)
        def deposit = new Deposit(account, amount)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == ERROR_MESSAGE_OPEN_DEPOSIT + amount
    }

    def "try to open a deposit for amount greater than the account balance"() {
        when:
        amount = 100
        account.setBalance(amount - 1)
        def deposit = new Deposit(account, amount)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == ERROR_MESSAGE_OPEN_DEPOSIT + amount
    }

    def "try to open a deposit for a null amount"() {
        when:
        account.setBalance(100.0)
        def deposit = new Deposit(account, amount)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == ERROR_MESSAGE_OPEN_DEPOSIT + amount
    }

    def "default deposit is open with today's date"() {
        when:
        account.setBalance(amount)
        deposit = new Deposit(account, amount)

        then:
        deposit.getOpenDate().isEqual(LocalDate.now())

        where:
        amount = 10.0
    }

    def "deposit cannot be closed before duration passed"() {
        when:
        account.setBalance(amount)
        def closeDate = openDate.plusMonths(durationInMonths - 1)
        deposit = new Deposit(account, amount, openDate, durationInMonths)
        deposit.finishDeposit(closeDate)

        then:
        RuntimeException ex = thrown()
        ex.message == ERROR_MESSAGE_CLOSE_DEPOSIT + closeDate

        where:
        amount = 10.0
        openDate = LocalDate.now()
        durationInMonths = 12
    }

    def "deposit cannot be closed when passed close date is null"() {
        when:
        account.setBalance(amount)
        def closeDate = null
        deposit = new Deposit(account, amount, openDate, durationInMonths)
        deposit.finishDeposit(closeDate)

        then:
        RuntimeException ex = thrown()
        ex.message == ERROR_MESSAGE_CLOSE_DEPOSIT + closeDate

        where:
        amount = 10.0
        openDate = LocalDate.now()
        durationInMonths = 12
    }

    def "when deposit closed balance is zero"() {
        when:
        account.setBalance(amount)
        def closeDate = openDate.plusMonths(durationInMonths)
        deposit = new Deposit(account, amount, openDate, durationInMonths)
        deposit.finishDeposit(closeDate)

        then:
        assertThat(deposit.getBalance()).isZero()

        where:
        amount = 10.0
        openDate = LocalDate.now()
        durationInMonths = 12
    }

    def "deposit can be closed when exactly duration time passed"() {
        when:
        account.setBalance(amount)
        def closeDate = openDate.plusMonths(durationInMonths)
        deposit = new Deposit(account, amount, openDate, durationInMonths)
        deposit.finishDeposit(closeDate)

        then:
        assertThat(deposit.isOpen()).isFalse()

        where:
        amount = 10.0
        openDate = LocalDate.now()
        durationInMonths = 12
    }

    def "deposit can be closed when exactly duration time plus one day passed"() {
        when:
        account.setBalance(amount)
        def closeDate = openDate.plusMonths(durationInMonths).plusDays(1)
        deposit = new Deposit(account, amount, openDate, durationInMonths)
        deposit.finishDeposit(closeDate)

        then:
        assertThat(deposit.isOpen()).isFalse()

        where:
        amount = 10.0
        openDate = LocalDate.now()
        durationInMonths = 12
    }

    def "when deposit created it is open"() {
        when:
        account.setBalance(amount)
        deposit = new Deposit(account, amount)

        then:
        assertThat(deposit.isOpen()).isTrue()

        where:
        amount = 10.0
    }

}