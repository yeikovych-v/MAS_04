package v.yeikovych.subset;

import v.yeikovych.dynamic.Member;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static v.yeikovych.util.ValidationUtils.*;

public class Event {
    private String eventName;
    // custom
    private LocalDateTime startTime;
    // custom
    private LocalDateTime endTime;
    private String location;
    private int maxAttendees;

    // Superset
    private Set<Member> registeredMembers = new HashSet<>();
    // Subset
    private Set<Member> attendedMembers = new HashSet<>();

    public Event(String eventName, LocalDateTime startTime, LocalDateTime endTime,
                 String location, int maxAttendees) {
        setEventName(eventName);
        setStartTime(startTime);
        setEndTime(endTime);
        setLocation(location);
        setMaxAttendees(maxAttendees);
    }

    public void registerMember(Member member) {
        throwIfFalse(() -> member != null, "Member cannot be null");
        throwIfFalse(() -> registeredMembers.size() >= maxAttendees, "Event has reached maximum capacity");

        if (!registeredMembers.contains(member)) {
            registeredMembers.add(member);
            member.registerForEvent(this);
        }
    }

    public void markMemberAttended(Member member) {
        throwIfNull(member, "Member cannot be null");
        throwIfFalse(() -> registeredMembers.contains(member), "Member must be registered for the event before being marked as attended");

        if (!attendedMembers.contains(member)) {
            attendedMembers.add(member);
            member.addAttendedEvent(this);
        }
    }

    public void unregisterMember(Member member) {
        throwIfFalse(() -> member != null, "Member cannot be null");

        if (registeredMembers.contains(member)) {
            registeredMembers.remove(member);
            member.unregisterFromEvent(this);
        }
    }

    public void unmarkMemberAttended(Member member) {
        throwIfNull(member, "Member cannot be null");

        if (attendedMembers.contains(member)) {
            attendedMembers.remove(member);
            member.removeAttendedEvent(this);
        }
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        throwIfFalse(() -> isValidString(eventName), "Event name must be a string");
        this.eventName = eventName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        throwIfFalse(() -> startTime != null, "Start time cannot be null");
        throwIfFalse(() -> startTime.isBefore(endTime), "Start time must be before end time");

        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        throwIfFalse(() -> endTime != null, "End time cannot be null");
        throwIfFalse(() -> endTime.isAfter(startTime), "End time must be after start time");

        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        throwIfFalse(() -> isValidString(location), "Location must be a string");
        this.location = location;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        throwIfFalse(() -> isPositive(maxAttendees), "Maximum attendees must be positive");
        throwIfFalse(() -> maxAttendees >= this.registeredMembers.size(),
                "Cannot reduce maximum attendees below current registration count");

        this.maxAttendees = maxAttendees;
    }

    public Set<Member> getRegisteredMembers() {
        return Collections.unmodifiableSet(registeredMembers);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return maxAttendees == event.maxAttendees
                && Objects.equals(eventName, event.eventName)
                && Objects.equals(startTime, event.startTime)
                && Objects.equals(endTime, event.endTime)
                && Objects.equals(location, event.location)
                && Objects.equals(registeredMembers, event.registeredMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, startTime, endTime, location, maxAttendees, registeredMembers);
    }
}
