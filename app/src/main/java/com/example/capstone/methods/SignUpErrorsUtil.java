package com.example.capstone.methods;

import com.parse.ParseException;

public class SignUpErrorsUtil {
    public static String errorMessage(int errorCode) {
        switch (errorCode) {
            case ParseException.EMAIL_TAKEN:
                return "This email has been taken already.";
            default:
                return "Error creating account.";
        }
    }
}
