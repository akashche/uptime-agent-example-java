package com.alexkasko.thermostat.uptime.server;

import com.alexkasko.thermostat.uptime.client.UptimeCommand;
import com.alexkasko.thermostat.uptime.client.UptimeResponse;
import com.alexkasko.thermostat.uptime.client.jaxb.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.Reader;
import java.io.Writer;

/**
 * Utility class to process JAXB messages
 */
class UptimeTransport {

    private static final JAXBContext JAXB;
    static {
        try {
            JAXB = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(new UptimeServerException("Error initializing JAXB context", e));
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends UptimeCommand> T readCommand(Reader reader) {
        try {
            return (T) JAXB.createUnmarshaller().unmarshal(reader);
        } catch (Exception e) {
            throw new UptimeServerException("Request deserialization error", e);
        }
    }

    static <T extends UptimeResponse> void writeResponse(Writer writer, T resp) {
        try {
            JAXB.createMarshaller().marshal(resp, writer);
        } catch (Exception e) {
            throw new UptimeServerException("Response serialization error", e);
        }
    }
}
