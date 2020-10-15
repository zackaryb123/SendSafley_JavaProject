package com.sendsafely;

import java.util.Optional;
import java.util.function.Supplier;

public class Utils {

    private Utils(){}

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            return Optional.ofNullable(resolver.get());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean isValidPhone(String phonenumber) {
        String regex = "^[0-9]{10}$";
        return phonenumber.matches(regex);
    }
}
