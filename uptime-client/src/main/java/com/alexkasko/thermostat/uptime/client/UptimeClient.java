package com.alexkasko.thermostat.uptime.client;

import com.alexkasko.thermostat.uptime.client.jaxb.ErrorResponse;
import com.alexkasko.thermostat.uptime.client.jaxb.ObjectFactory;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAction;
import com.alexkasko.thermostat.uptime.client.jaxb.UptimeAndIdleCommand;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.Charset;

/**
 * HTTP client for Uptime server. Use methods {@link #get(com.alexkasko.thermostat.uptime.client.jaxb.UptimeAction)} or
 * {@link #post(com.alexkasko.thermostat.uptime.client.jaxb.UptimeAction, UptimeCommand)} t} to send commands to server.
 */
public class UptimeClient {

    private enum HttpMethod {GET, POST, PUT, DELETE}

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String EMPTY_STRING = "";
    private static final JAXBContext JAXB;
    static {
        try {
            JAXB = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(new UptimeClientException("Error initializing JAXB context", e));
        }
    }
    private static final String URL_TEMPLATE = "http://%s:%d/actions/%s";

    private final CloseableHttpClient http;
    private final String host;
    private final int port;

    /**
     * Constructor
     *
     * @param host Uptime agent hostname
     * @param port Uptime agent TCP port
     */
    public UptimeClient(String host, int port) {
        if (null == host || 0 == host.length()) throw new IllegalArgumentException("Empty host specified");
        this.host = host;
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Invalid port specified: [" + port + "]");
        this.port = port;
        this.http = HttpClients.createDefault();
    }

    /**
     * Send GET request to Uptime agent
     *
     * @param action server action name
     * @param <R> response type
     * @return response object from server
     * @throws UptimeClientException
     */
    public <R extends UptimeResponse> R get(UptimeAction action) throws UptimeClientException {
        return send(action, HttpMethod.GET, new UptimeAndIdleCommand());
    }

    /**
     * Send POST request to Uptime agent
     *
     * @param action server action name
     * @param command command to server
     * @param <C> command type
     * @param <R> response type
     * @return response object from server
     * @throws UptimeClientException
     */
    public <C extends UptimeCommand, R extends UptimeResponse> R post(UptimeAction action, C command) throws UptimeClientException {
        return send(action, HttpMethod.POST, command);
    }

    @SuppressWarnings("unchecked") // JAXB unmarshal
    private  <C extends UptimeCommand, R extends UptimeResponse>
    R send(UptimeAction action, HttpMethod method, C command) throws UptimeClientException {
        if (null == action) throw new IllegalArgumentException("Null path specified");
        if (null == method) throw new IllegalArgumentException("Null method specified");
        if (null == command) throw new IllegalArgumentException("Null command specified");
        String url = null;
        CloseableHttpResponse resp = null;
        Reader re = null;
        try {
            url = String.format(URL_TEMPLATE, host, port, action.value());
            HttpUriRequest req = createRequest(url, method, command);
            resp = http.execute(req);
            HttpEntity entity = resp.getEntity();
            if(200 != resp.getStatusLine().getStatusCode()) {
                String resptext = safeString(entity);
                String msg = "Lookup error, url: [" + url + "] client: [" + this + "]," +
                        " response status: [" + resp.getStatusLine() + "], response: [" + resptext + "]";
                throw new UptimeClientException(msg);
            }
            re = new BufferedReader(new InputStreamReader(entity.getContent(), UTF_8));
            UptimeResponse fr = (UptimeResponse) JAXB.createUnmarshaller().unmarshal(re);
            if (fr instanceof ErrorResponse) throw new UptimeClientException("Error response from server: [" + fr + "]");
            return (R) fr;
        } catch (Exception e) {
            throw e instanceof UptimeClientException ? (UptimeClientException) e :
                    new UptimeClientException("Error, url: [" + url + "], command: [" + command + "]", e);
        } finally {
            closeQuietly(re);
            closeQuietly(resp);
        }

    }

    private HttpUriRequest createRequest(String url, HttpMethod method, UptimeCommand cmd) throws JAXBException, UptimeClientException {
        switch (method) {
            case GET: return new HttpGet(url);
            case POST:
                HttpPost req = new HttpPost(url);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                java.io.Writer wr = new OutputStreamWriter(baos, UTF_8);
                JAXB.createMarshaller().marshal(cmd, wr);
                ContentType ct = ContentType.create("application/xml");
                HttpEntity en = new ByteArrayEntity(baos.toByteArray(), ct);
                req.setEntity(en);
                return req;
            case PUT:
            case DELETE:
            default: throw new UptimeClientException("Unsupported method: [" + method + "]");
        }
    }

    private static String safeString(HttpEntity en) {
        try {
            if (null == en) return EMPTY_STRING;
            return EntityUtils.toString(en, UTF_8.name());
        } catch (Exception e) {
            return EMPTY_STRING;
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (null == closeable) return;
        try {
            closeable.close();
        } catch (Exception e) {
            // ignore
        }
    }
}
