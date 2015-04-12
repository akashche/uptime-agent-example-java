package com.alexkasko.thermostat.uptime.client;

import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAction;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleFormattedCommand;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleFormattedResponse;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleResponse;
import org.junit.Test;

/**
 * Simple test
 */
public class UptimeTest {

    @Test
    public void dummy() {
        // noop
    }

//    @Test
    public void test() {
        UptimeClient client = new UptimeClient("127.0.0.1", 8881);
        // simple GET
        UptimeAndIdleResponse resp = client.get(UptimeAction.UPTIME_AND_IDLE);
        System.out.println(resp);
        // formatted POST
        UptimeCommand cmd = new UptimeAndIdleFormattedCommand("#.000000");
        UptimeAndIdleFormattedResponse rf = client.post(UptimeAction.UPTIME_AND_IDLE_FORMATTED, cmd);
        System.out.println(rf);
    }

}
