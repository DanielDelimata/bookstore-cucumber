package eu.delimata.bookstore.support;


import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class HttpLogFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpLogFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        String trace = UUID.randomUUID().toString().substring(0, 8);

        // → REQUEST
        String method = req.getMethod();
        String uri = req.getURI(); // pełne URI z query
        String body = req.getBody() == null ? "" : req.getBody().toString();

        log.info("[{}] → {} {}", trace, method, uri);
        if (!body.isBlank()) {
            log.info("[{}] payload:\n{}", trace, pretty(body));
        }

        long t0 = System.nanoTime();
        Response response = ctx.next(req, res);
        long durMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);

        // ← RESPONSE
        log.info("[{}] ← {} {} ({} ms)", trace, response.getStatusCode(), response.getStatusLine(), durMs);
        String respBody = safeBody(response);
        if (!respBody.isBlank()) {
            log.info("[{}] response:\n{}", trace, pretty(respBody));
        }

        return response;
    }

    private static String safeBody(Response r) {
        try { return r.getBody() == null ? "" : r.getBody().asPrettyString(); }
        catch (Exception e) { return ""; }
    }

    // bardzo prosty pretty – nie wywala się na nie-JSON, tylko zwraca oryginał
    private static String pretty(String s) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            Object tree = om.readValue(s, Object.class);
            return om.writerWithDefaultPrettyPrinter().writeValueAsString(tree);
        } catch (Exception ignore) {
            return s;
        }
    }
}
