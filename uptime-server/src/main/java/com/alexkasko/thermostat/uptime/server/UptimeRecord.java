package com.alexkasko.thermostat.uptime.server;

import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Uptime record class
 */
class UptimeRecord {
    private final float uptime;
    private final float idletime;

    UptimeRecord(float uptime, float idletime) {
        this.uptime = uptime;
        this.idletime = idletime;
    }

    float getUptime() {
        return uptime;
    }

    float getIdletime() {
        return idletime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("uptime", uptime)
                .append("idletime", idletime)
                .toString();
    }
}
