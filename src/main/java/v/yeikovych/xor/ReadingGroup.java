package v.yeikovych.xor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import v.yeikovych.bag.BookCategory;
import v.yeikovych.dynamic.Member;

import static v.yeikovych.util.ValidationUtils.*;

public class ReadingGroup {
    private String groupName;
    private BookCategory focusCategory;
    private int maxCapacity;

    // XOR
    private Set<Member> participants = new HashSet<>();

    public ReadingGroup(String groupName, BookCategory focusCategory, int maxCapacity) {
        setGroupName(groupName);
        setFocusCategory(focusCategory);
        setMaxCapacity(maxCapacity);
    }

    public void addParticipant(Member member) {
        throwIfNull(member, "Member cannot be null");

        throwIfFalse(() -> participants.size() < maxCapacity,
                "Reading group has reached maximum capacity");

        if (!this.participants.contains(member)) {
            this.participants.add(member);
            member.setReadingGroup(this);
        }
    }

    public void removeParticipant(Member member) {
        throwIfNull(member, "Member cannot be null");

        if (this.participants.remove(member)) {
            member.setReadingGroup(null);
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        throwIfFalse(() -> isValidString(groupName),
                "Group name cannot be null or empty");
        this.groupName = groupName;
    }

    public BookCategory getFocusCategory() {
        return focusCategory;
    }

    public void setFocusCategory(BookCategory focusCategory) {
        throwIfNull(focusCategory, "Focus category cannot be null");
        this.focusCategory = focusCategory;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        throwIfFalse(() -> isPositive(maxCapacity),
                "Maximum capacity must be positive");

        if (this.participants != null) {
            throwIfFalse(() -> maxCapacity >= this.participants.size(),
                    "Cannot reduce maximum capacity below current participant count");
        }

        this.maxCapacity = maxCapacity;
    }

    public Set<Member> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }
}
