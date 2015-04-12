package com.alexkasko.thermostat.uptime.server;

/**
 * Module specific exception
 */
public class UptimeServerException extends RuntimeException {

    public UptimeServerException(String message) {
        super(message);
    }

    public UptimeServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
