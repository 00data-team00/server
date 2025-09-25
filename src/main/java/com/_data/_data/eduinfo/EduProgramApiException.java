package com._data._data.eduinfo;

public class EduProgramApiException extends RuntimeException {

    public EduProgramApiException(String message) {
        super(message);
    }

    public EduProgramApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public EduProgramApiException(Throwable cause) {
        super(cause);
    }
}
