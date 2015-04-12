package com.alexkasko.thermostat.uptime.client;

/**
 * Module specific exception
 */
public class UptimeClientException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public UptimeClientException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public UptimeClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
