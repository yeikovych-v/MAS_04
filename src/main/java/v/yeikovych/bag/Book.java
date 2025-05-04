package v.yeikovych.bag;

import v.yeikovych.ordered.Loan;
import v.yeikovych.ordered.Reservation;
import v.yeikovych.util.Extent;
import v.yeikovych.util.SerializationUtil;

import java.time.LocalDate;
import java.util.*;

import static v.yeikovych.util.ValidationUtils.*;

public class Book implements Extent {
    // unique
    private String isbn;
    private String title;
    private String author;
    // static
    private int publicationYear;
    private BookCategory category;
    private int pageCount;
    // static
    private double rating;

    // history
    private List<Loan> loanHistory = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    private static List<Book> extent = new ArrayList<>();

    public Book(String title, String author, int publicationYear,
                BookCategory category, int pageCount) {
        setIsbn(isbn);
        setTitle(title);
        setAuthor(author);
        setPublicationYear(publicationYear);
        setCategory(category);
        setPageCount(pageCount);
        this.rating = 0.0;
        SerializationUtil.registerExtent(extent, Book.class);
        SerializationUtil.writeExtent();
    }

    public void setIsbn(String isbn) {
        throwIfNull(isbn, "Isbn cannot be null");

        throwIfFalse(() -> isValidIsbn(isbn), "Invalid ISBN format");

        for (Book book : extent) {
            if (book != this && isbn.equals(book.getIsbn())) {
                throw new IllegalArgumentException("ISBN already exists: " + isbn);
            }
        }

        this.isbn = isbn;
        SerializationUtil.writeExtent();
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = LocalDate.now().getYear();
        throwIfFalse(() -> publicationYear >= 1500 && publicationYear <= currentYear,
                "Publication year must be between 1500 and " + currentYear);
        this.publicationYear = publicationYear;
        SerializationUtil.writeExtent();
    }

    public void setRating(double rating) {
        throwIfFalse(() -> rating > 0 && rating <= 5, "Rating must be between 0 and 5");
        this.rating = rating;
        SerializationUtil.writeExtent();
    }

    public void setTitle(String title) {
        throwIfFalse(() -> isValidName(title), "Title must not be null or empty");
        this.title = title;
        SerializationUtil.writeExtent();
    }

    public void setAuthor(String author) {
        throwIfFalse(() -> isValidName(author), "Author name must not be null or empty");
        this.author = author;
        SerializationUtil.writeExtent();
    }

    public void setCategory(BookCategory category) {
        throwIfNull(category, "Category cannot be null");
        this.category = category;
        SerializationUtil.writeExtent();
    }

    public void setPageCount(int pageCount) {
        throwIfFalse(() -> isPositive(pageCount), "Page count must be positive");
        this.pageCount = pageCount;
        SerializationUtil.writeExtent();
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public BookCategory getCategory() {
        return category;
    }

    public int getPageCount() {
        return pageCount;
    }

    public double getRating() {
        return rating;
    }

    public static List<Book> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    // ordered
    public void addReservation(Reservation reservation) {
        throwIfNull(reservation, "Reservation cannot be null");

        if (!reservations.contains(reservation)) {
            this.reservations.add(reservation);
            this.reservations.sort(Comparator.comparing(Reservation::getReservationDate));
            reservation.setBook(this);
            SerializationUtil.writeExtent();
        }
    }

    public void removeReservation(Reservation reservation) {
        throwIfNull(reservation, "Reservation cannot be null");

        if (this.reservations.remove(reservation)) {
            reservation.setBook(null);
            SerializationUtil.writeExtent();
        }
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(this.reservations);
    }

    public void addLoanToHistory(Loan loan) {
        throwIfNull(loan, "Loan cannot be null");

        this.loanHistory.add(loan);

        if (loan.getBook() != this) {
            loan.setBook(this);
        }

        SerializationUtil.writeExtent();
    }

    public void removeLoanFromHistory(Loan loan) {
        throwIfNull(loan, "Loan cannot be null");

        this.loanHistory.remove(loan);

        if (loan.getBook() == this) {
            loan.setBook(null);
        }

        SerializationUtil.writeExtent();
    }

    public List<Loan> getLoanHistory() {
        return Collections.unmodifiableList(this.loanHistory);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return publicationYear == book.publicationYear
                && pageCount == book.pageCount
                && Double.compare(rating, book.rating) == 0
                && Objects.equals(title, book.title)
                && Objects.equals(author, book.author)
                && category == book.category
                && Objects.equals(loanHistory, book.loanHistory)
                && Objects.equals(reservations, book.reservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publicationYear, category, pageCount, rating, loanHistory, reservations);
    }
}
