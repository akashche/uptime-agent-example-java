package com.alexkasko.thermostat.uptime.server;

import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleFormattedCommand;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleFormattedResponse;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Example of agent servlet, currently there is no URL->action dispatching logic,
 * some generic dispatcher can be used, for example url_regex->action like in Django.
 * Also this example implementation lacks errors handling.
 */
public class UptimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UptimeRecord rec = new UptimeAccessor().readUptime();
        UptimeAndIdleResponse response = new UptimeAndIdleResponse(rec.getUptime(), rec.getIdletime());
        UptimeTransport.writeResponse(resp.getWriter(), response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UptimeAndIdleFormattedCommand cmd = UptimeTransport.readCommand(req.getReader());
        UptimeRecord rec = new UptimeAccessor().readUptime();
        DecimalFormat fmt = new DecimalFormat(cmd.getDecimalFormat());
        String uptime = fmt.format(rec.getUptime());
        String idletime = fmt.format(rec.getIdletime());
        UptimeAndIdleFormattedResponse response = new UptimeAndIdleFormattedResponse(uptime, idletime);
        UptimeTransport.writeResponse(resp.getWriter(), response);
    }

}
