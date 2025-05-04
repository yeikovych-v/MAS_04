package v.yeikovych.xor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import v.yeikovych.dynamic.Member;
import v.yeikovych.dynamic.MembershipStatus;

import static v.yeikovych.util.ValidationUtils.*;

public class Club {
    private String clubName;
    private String description;
    private MembershipStatus requiredStatus;

    // XOR
    private Set<Member> members = new HashSet<>();

    public Club(String clubName, String description, MembershipStatus requiredStatus) {
        setClubName(clubName);
        setDescription(description);
        setRequiredStatus(requiredStatus);
    }

    public void addMember(Member member) {
        throwIfNull(member, "Member cannot be null");

        if (requiredStatus != null) {
            throwIfFalse(() -> member.getStatus().compareTo(requiredStatus) >= 0,
                    "This club requires " + requiredStatus + " status or higher");
        }

        if (!this.members.contains(member)) {
            this.members.add(member);
            member.setClub(this);
        }
    }

    public void removeMember(Member member) {
        throwIfNull(member, "Member cannot be null");

        if (this.members.remove(member)) {
            member.setClub(null);
        }
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        throwIfFalse(() -> isValidString(clubName), "Club name cannot be null or empty");
        this.clubName = clubName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        throwIfFalse(() -> isValidString(description), "Description cannot be null or empty");
        this.description = description;
    }

    public MembershipStatus getRequiredStatus() {
        return requiredStatus;
    }

    public void setRequiredStatus(MembershipStatus requiredStatus) {
        this.requiredStatus = requiredStatus;
    }

    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
