package v.yeikovych.dynamic;

import v.yeikovych.ordered.Loan;
import v.yeikovych.ordered.Reservation;
import v.yeikovych.staticc.Person;
import v.yeikovych.subset.Event;
import v.yeikovych.util.ValidationException;
import v.yeikovych.xor.Club;
import v.yeikovych.xor.ReadingGroup;

import java.time.LocalDate;
import java.util.*;

import static v.yeikovych.util.ValidationUtils.*;

public class Member extends Person {
    private LocalDate registrationDate;
    // dynamic depends on borrowed items
    private MembershipStatus status;
    // dynamic
    private int borrowedItemsCount;
    private boolean isBlacklisted;

    // ordered
    private List<Loan> loans = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    private Set<Event> registeredEvents = new HashSet<>();
    private Set<Event> attendedEvents = new HashSet<>();

    // XOR
    private Club club;
    private ReadingGroup readingGroup;

    public Member(String firstName, String lastName, LocalDate dateOfBirth, String email,
                  String phoneNumber, MembershipStatus status) {
        super(firstName, lastName, dateOfBirth, email, phoneNumber);
        this.registrationDate = LocalDate.now();
        setStatus(status);
        this.borrowedItemsCount = 0;
        this.isBlacklisted = false;
    }

    public void setBorrowedItemsCount(int borrowedItemsCount) {
        throwIfFalse(() -> isNegative(borrowedItemsCount), "Borrowed items count cannot be negative");

        int maxAllowed = getMaxBorrowedItemsForStatus(status);
        if (borrowedItemsCount > maxAllowed) {
            throw new ValidationException("Exceeded maximum allowed borrowed items (" +
                    maxAllowed + ") for member status: " + status);
        }

        this.borrowedItemsCount = borrowedItemsCount;
    }

    public void incrementBorrowedItems() {
        setBorrowedItemsCount(this.borrowedItemsCount + 1);
    }

    public void decrementBorrowedItems() {
        setBorrowedItemsCount(this.borrowedItemsCount - 1);
    }

    public void setStatus(MembershipStatus status) {
        throwIfFalse(() -> status != null, "Status cannot be null");

        if (this.status != null && status.ordinal() < this.status.ordinal()) {
            int newMaxAllowed = getMaxBorrowedItemsForStatus(status);
            if (this.borrowedItemsCount > newMaxAllowed) {
                throw new IllegalArgumentException("Cannot downgrade status: member has " +
                        this.borrowedItemsCount + " borrowed items, but new status allows only " +
                        newMaxAllowed);
            }
        }

        this.status = status;
    }

    private int getMaxBorrowedItemsForStatus(MembershipStatus status) {
        return switch (status) {
            case REGULAR -> 5;
            case PREMIUM -> 10;
            case VIP -> 15;
        };
    }

    public void setBlacklisted(boolean blacklisted) {
        this.isBlacklisted = blacklisted;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public int getBorrowedItemsCount() {
        return borrowedItemsCount;
    }

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public void registerForEvent(Event event) {
        throwIfNull(event, "Event cannot be null");

        if (!registeredEvents.contains(event)) {
            registeredEvents.add(event);
            event.registerMember(this);
        }
    }

    public void unregisterFromEvent(Event event) {
        throwIfNull(event, "Event cannot be null");

        if (registeredEvents.contains(event)) {
            registeredEvents.remove(event);
            event.unregisterMember(this);
        }
    }

    public void addAttendedEvent(Event event) {
        throwIfNull(event, "Event cannot be null");
        throwIfFalse(() -> registeredEvents.contains(event),
                "Member must be registered for the event before being marked as attended");

        if (!attendedEvents.contains(event)) {
            attendedEvents.add(event);
            event.markMemberAttended(this);
        }
    }

    public void removeAttendedEvent(Event event) {
        throwIfNull(event, "Event cannot be null");

        if (registeredEvents.contains(event)) {
            attendedEvents.remove(event);
            event.unmarkMemberAttended(this);
        }
    }

    public Set<Event> getAttendedEvents() {
        return Collections.unmodifiableSet(attendedEvents);
    }

    public Set<Event> getRegisteredEvents() {
        return Collections.unmodifiableSet(registeredEvents);
    }

    public List<Loan> getLoans() {
        return loans.stream().sorted(Comparator.comparing(Loan::getDueDate)).toList();
    }

    public void addLoan(Loan loan) {
        throwIfNull(loan, "Loan cannot be null");

        if (!loans.contains(loan)) {
            this.loans.add(loan);
            loan.setMember(this);
            incrementBorrowedItems();
        }
    }

    public void removeLoan(Loan loan) {
        throwIfNull(loan, "Loan cannot be null");

        if (loans.contains(loan)) {
            this.loans.remove(loan);
            loan.setMember(null);
            decrementBorrowedItems();
        }
    }

    public void setClub(Club club) {
        if (club != null) {
            throwIfFalse(() -> this.readingGroup == null,
                    "Member cannot be in both a Club and a Reading Group");
        }

        if (this.club != null && this.club != club) {
            this.club.removeMember(this);
        }

        this.club = club;

        if (club != null) {
            club.addMember(this);
        }
    }

    public void setReadingGroup(ReadingGroup readingGroup) {
        if (readingGroup != null) {
            throwIfFalse(() -> this.club == null,
                    "Member cannot be in both a Club and a Reading Group");
        }

        if (this.readingGroup != null && this.readingGroup != readingGroup) {
            this.readingGroup.removeParticipant(this);
        }

        this.readingGroup = readingGroup;

        if (readingGroup != null) {
            readingGroup.addParticipant(this);
        }
    }

    public Club getClub() {
        return club;
    }

    public ReadingGroup getReadingGroup() {
        return readingGroup;
    }

    public void addReservation(Reservation reservation) {
        throwIfNull(reservation, "Reservation cannot be null");

        if (!this.reservations.contains(reservation)) {
            this.reservations.add(reservation);
            reservation.setMember(this);
        }
    }

    public void removeReservation(Reservation reservation) {
        throwIfNull(reservation, "Reservation cannot be null");

        if (this.reservations.remove(reservation)) {
            reservation.setMember(null);
        }
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(this.reservations);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Member member = (Member) o;
        return borrowedItemsCount == member.borrowedItemsCount
                && isBlacklisted == member.isBlacklisted
                && Objects.equals(registrationDate, member.registrationDate)
                && status == member.status && Objects.equals(loans, member.loans)
                && Objects.equals(reservations, member.reservations)
                && Objects.equals(registeredEvents, member.registeredEvents)
                && Objects.equals(club, member.club)
                && Objects.equals(readingGroup, member.readingGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), registrationDate, status, borrowedItemsCount, isBlacklisted, loans, reservations, registeredEvents, club, readingGroup);
    }
}
