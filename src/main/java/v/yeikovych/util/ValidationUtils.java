package v.yeikovych.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isValidString(String string) {
        return string != null && !string.isBlank();
    }

    public static boolean isValidAge(int age) {
        return age >= 0 && age <= 110;
    }

    public static boolean isValidName(String name) {
        return isValidString(name) && name.trim().length() > 1;
    }

    public static boolean isPositiveOrZero(double number) {
        return number >= 0;
    }

    public static boolean isPositiveOrZero(int number) {
        return number >= 0;
    }

    public static boolean isPositiveOrZero(long number) {
        return number >= 0;
    }

    public static boolean isPositiveOrZero(BigDecimal number) {
        return number.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isPositive(long number) {
        return number > 0;
    }

    public static boolean isPositive(BigDecimal number) {
        return number.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isMultiAttribute(List<?> multiAttribute) {
        return multiAttribute != null && !multiAttribute.isEmpty() && !anyNull(multiAttribute);
    }

    public static boolean isRequiredCollection(Collection<?> collection) {
        return collection != null && allNotNull(collection);
    }

    public static boolean allNotNull(Collection<?> collection) {
        return collection.stream().noneMatch(Objects::isNull);
    }

    public static boolean isValidNullableName(String name) {
        if (name == null) return true;
        return isValidName(name);
    }

    public static void throwIfNull(Object object) throws ValidationException {
        if (Objects.isNull(object)) {
            throw new ValidationException("Validation failed: null is not allowed");
        }
    }

    public static void throwIfNull(Object object, String message) throws ValidationException {
        if (Objects.isNull(object)) {
            throw new ValidationException(message);
        }
    }

    public static boolean allTrue(BooleanSupplier... conditions) {
        for (BooleanSupplier condition : conditions) {
            if (!condition.getAsBoolean()) {
                return false;
            }
        }
        return true;
    }

    public static void throwIfAnyFalse(BooleanSupplier... conditions) throws ValidationException {
        if (!allTrue(conditions)) {
            throw new ValidationException("Validation failed");
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank() || email.length() > 320) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() >= 10 && phoneNumber.length() <= 15;
    }

    public static boolean anyNull(List<?> list) {
        return list.stream().anyMatch(Objects::isNull);
    }

    public static void throwIfFalse(BooleanSupplier condition) throws ValidationException {
        throwIfFalse(condition, "Validation failed");
    }

    public static void throwIfFalse(BooleanSupplier condition, String message) throws ValidationException {
        if (!condition.getAsBoolean()) {
            throw new ValidationException(message);
        }
    }

    public static boolean isValidDayOfMonth(int day) {
        return day >= 1 && day <= 31;
    }

    public static boolean isValidDateOfBirth(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }

    public static boolean isNegative(int number) {
        return number < 0;
    }

    public static boolean isValidIsbn(String isbn) {
        String isbnPattern = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:\\d+[- ]){3})[- 0-9X]{13}$|97[89]\\d{10}$|" +
                "(?=(?:\\d+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?\\d{1,5}[- ]?\\d+[- ]?\\d+[- ]?[0-9X]$";
        return isbn.matches(isbnPattern);
    }
}
