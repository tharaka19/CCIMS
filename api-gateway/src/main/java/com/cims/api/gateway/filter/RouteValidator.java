package com.cims.api.gateway.filter;

import com.cims.api.gateway.constants.EndPoints;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * The RouteValidator class is responsible for validating if a given request is a secured request or not.
 * It contains a list of open API endpoints, which do not require authorization.
 */
@Component
public class RouteValidator {

    /**
     * A predicate that checks whether the request is secured or not by matching the request URI with open API endpoints.
     */
    public Predicate<ServerHttpRequest> isSecured =
            request -> EndPoints.OPEN_API_END_POINTS
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
