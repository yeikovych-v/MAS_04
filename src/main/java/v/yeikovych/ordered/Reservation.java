package v.yeikovych.ordered;


import v.yeikovych.bag.Book;
import v.yeikovych.dynamic.Member;

import java.time.LocalDate;

import static v.yeikovych.util.ValidationUtils.*;

public class Reservation {
    private LocalDate reservationDate;
    private LocalDate expirationDate;
    private ReservationStatus status;

    private Member member;
    private Book book;

    public Reservation(Member member, Book book, LocalDate reservationDate, LocalDate expirationDate) {
        throwIfNull(reservationDate, "Reservation date cannot be null");
        throwIfNull(expirationDate, "Expiration date cannot be null");
        throwIfFalse(() -> !expirationDate.isBefore(reservationDate),
                "Expiration date cannot be before reservation date");

        setMember(member);
        setBook(book);
        this.reservationDate = reservationDate;
        this.expirationDate = expirationDate;
        this.status = ReservationStatus.PENDING;
    }

    public void checkIfExpired() {
        if (status == ReservationStatus.PENDING && LocalDate.now().isAfter(expirationDate)) {
            this.status = ReservationStatus.EXPIRED;
        }
    }

    public void fulfill() {
        throwIfFalse(() -> status == ReservationStatus.PENDING,
                "Only pending reservations can be fulfilled");
        this.status = ReservationStatus.FULFILLED;
    }

    public void cancel() {
        throwIfFalse(() -> status == ReservationStatus.PENDING,
                "Only pending reservations can be canceled");
        this.status = ReservationStatus.CANCELED;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public void setMember(Member member) {
        throwIfNull(member, "Member cannot be null");

        if (this.member != null && this.member != member) {
            this.member.removeReservation(this);
        }

        this.member = member;

        if (member != null) {
            member.addReservation(this);
        }
    }

    public void setBook(Book book) {
        throwIfNull(book, "Book cannot be null");

        if (this.book != null && this.book != book) {
            this.book.removeReservation(this);
        }

        this.book = book;

        if (book != null) {
            book.addReservation(this);
        }
    }
}
