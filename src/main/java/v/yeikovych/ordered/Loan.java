package v.yeikovych.ordered;

import java.time.LocalDate;

import v.yeikovych.bag.Book;
import v.yeikovych.dynamic.Member;

import static v.yeikovych.util.ValidationUtils.*;

public class Loan {
    // custom
    private LocalDate loanDate;
    // custom
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isOverdue;
    private double lateFee;

    private Member member;
    private Book book;

    public Loan(Member member, Book book, LocalDate loanDate, LocalDate dueDate) {
        throwIfNull(member, "Member cannot be null");
        throwIfNull(book, "Book cannot be null");
        throwIfNull(loanDate, "Loan date cannot be null");
        throwIfNull(dueDate, "Due date cannot be null");
        throwIfFalse(() -> !dueDate.isBefore(loanDate),
                "Due date cannot be before loan date");

        setMember(member);
        setBook(book);
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.isOverdue = false;
        this.lateFee = 0.0;
    }

    public void checkIfOverdue() {
        if (returnDate == null || LocalDate.now().isAfter(dueDate)) {
            this.isOverdue = true;
            long daysLate = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
            this.lateFee = daysLate * 1.0;
        }
    }

    public void returnBook(LocalDate returnDate) {
        throwIfNull(returnDate, "Return date cannot be null");
        throwIfFalse(() -> !returnDate.isBefore(loanDate),
                "Return date cannot be before loan date");

        this.returnDate = returnDate;

        checkIfOverdue();
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public double getLateFee() {
        return lateFee;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public void setMember(Member member) {
        if (this.member != null && this.member != member) {
            this.member.removeLoan(this);
        }

        this.member = member;

        if (member != null && !member.getLoans().contains(this)) {
            member.addLoan(this);
        }
    }

    public void setBook(Book book) {
        if (this.book != null && this.book != book) {
            this.book.removeLoanFromHistory(this);
        }

        this.book = book;

        if (book != null && !book.getLoanHistory().contains(this)) {
            book.addLoanToHistory(this);
        }
    }
}
