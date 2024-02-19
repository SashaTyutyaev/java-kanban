package main.exceptions;

import java.io.IOException;

public class ValidationException extends IOException {
    public ValidationException() {};

    public ValidationException(String message) {
        super.getMessage();
    }
}
