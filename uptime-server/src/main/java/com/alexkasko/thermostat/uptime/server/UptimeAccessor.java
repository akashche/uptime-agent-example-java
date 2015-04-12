package com.alexkasko.thermostat.uptime.server;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Basic implementation of /proc/uptime access
 */
class UptimeAccessor {

    UptimeRecord readUptime() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/proc/uptime");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("ASCII")));
            String st = br.readLine();
            return parse(st);
        } catch (IOException e) {
            throw new UptimeServerException("System uptime access error", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    // checks can be improved
    private UptimeRecord parse(String st) throws IOException {
        String[] parts = st.split("\\s+");
        if(2 != parts.length) throw new IOException("Invalid uptime string: [" + st + "]");
        float uptime = Float.parseFloat(parts[0]);
        float idletime = Float.parseFloat(parts[1]);
        return new UptimeRecord(uptime, idletime);
    }

}
