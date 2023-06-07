package com.cims.api.gateway.filter;

import com.cims.api.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Gateway filter for authentication of requests.
 * Checks whether the request is secure and contains a valid JWT token in the Authorization header.
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Constructor that sets up the filter and its configuration class.
     */
    public AuthenticationFilter() {
        super(Config.class);
    }

    /**
     * Creates and returns a new instance of a GatewayFilter that performs authentication.
     *
     * @param config the configuration object for the filter (not used)
     * @return a new GatewayFilter instance that performs authentication
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                //header contain token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    //REST call to AUTH service
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    throw new RuntimeException("unauthorized access to application");
                }
            }
            return chain.filter(exchange);
        }));
    }

    /**
     * Configuration class for the AuthenticationFilter.
     * (Currently empty, as this filter doesn't require any additional configuration)
     */
    public static class Config {
    }
}
