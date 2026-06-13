package com.example.usermanagement.common;

public class StatusCode {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int BAD_REQUEST = 400;

    public static final int USER_NOT_FOUND = 1001;
    public static final int USER_ALREADY_EXISTS = 1002;
    public static final int PASSWORD_ERROR = 1003;
    public static final int USER_DISABLED = 1004;
    public static final int TOKEN_EXPIRED = 1005;
    public static final int TOKEN_INVALID = 1006;
}
