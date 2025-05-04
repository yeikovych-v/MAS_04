package v.yeikovych.staticc;

import v.yeikovych.util.Extent;
import v.yeikovych.util.SerializationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static v.yeikovych.util.ValidationUtils.*;

public class Person implements Extent {
    private String firstName;
    private String lastName;
    // static
    private LocalDate dateOfBirth;
    // unique
    private String email;
    // static
    private String phoneNumber;

    private static List<Person> extent = new ArrayList<>();

    public Person(String firstName, String lastName, LocalDate dateOfBirth, String email, String phoneNumber) {
        setFirstName(firstName);
        setLastName(lastName);
        setDateOfBirth(dateOfBirth);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        SerializationUtil.registerExtent(extent, Person.class);
        SerializationUtil.writeExtent();
    }

    public void setPhoneNumber(String phoneNumber) {
        throwIfFalse(() -> isValidPhoneNumber(phoneNumber), "Phone number is not valid");
        this.phoneNumber = phoneNumber;
        SerializationUtil.writeExtent();
    }

    public void setEmail(String email) {
        throwIfFalse(() -> isValidEmail(email), "Email is not valid");

        for (Person person : extent) {
            if (person != this && email.equals(person.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + email);
            }
        }

        this.email = email;
        SerializationUtil.writeExtent();
    }

    public void setFirstName(String firstName) {
        throwIfFalse(() -> isValidName(firstName), "First name is not valid");
        this.firstName = firstName;
        SerializationUtil.writeExtent();
    }

    public void setLastName(String lastName) {
        throwIfFalse(() -> isValidName(lastName), "Last name is not valid");
        this.lastName = lastName;
        SerializationUtil.writeExtent();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        throwIfFalse(() -> isValidDateOfBirth(dateOfBirth), "Date of birth is not valid");
        this.dateOfBirth = dateOfBirth;
        SerializationUtil.writeExtent();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public static List<Person> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}