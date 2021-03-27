package org.crashoverride.battleship.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
@PropertySource("classpath:extra.properties")
public class RestClient {

    private static final String AUTH_URL_SUFFIX = "/api/authenticate";
    private static final String REGISTER_TOURNAMENT_URL_SUFFIX = "/api/tournaments/TOURNAMENT_ID/teams";
    private static final String PLACE_BATTLESHIP_URL_SUFFIX = "/api/tournaments/TOURNAMENT_ID/battleships";
    private static final String PLACE_SHIP_PAYLOAD = "{\"gameId\":\"INPUT_GAME_ID\",\"x\":INPUT_X,\"y\":INPUT_Y,\"direction\":\"INPUT_DIRECTION\"}";

    private String accessToken = "";
    private final String tournamentId;
    private final String baseUrl;
    private final UriSpec<RequestBodySpec> uriSpec;
    private final Retry retry;
    private final String authPayload;

    public RestClient(
            @Value("${rest.base.url}") String baseUrl,
            @Value("${tournament.id}") String tournamentId,
            @Value("${auth.username}") String authUser,
            @Value("${auth.password}") String authPass
    ) {
        this.tournamentId = tournamentId;
        this.baseUrl = baseUrl;

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        this.uriSpec = webClient.post();
        this.retry = Retry.backoff(3, Duration.ofSeconds(1)).filter(t -> t instanceof TimeoutException);
        this.authPayload = "{\"username\":\"" + authUser + "\",\"password\":\"" + authPass + "\"}";
    }

    public String getAccessToken() {
        if (accessToken.isEmpty()) {
            AuthResponse rsvp = Optional.ofNullable(call(AUTH_URL_SUFFIX, authPayload, false)
                    .exchangeToMono(this::checkResponseAuth)
                    .retryWhen(this.retry)
                    .block())
                    .orElse(new AuthResponse(""));
            this.accessToken = rsvp.getToken();

        }

        return accessToken;
    }

    public void registerTournament() {
        getAccessToken();
        final String registerTournamentUrl = baseUrl.concat(REGISTER_TOURNAMENT_URL_SUFFIX.replaceAll("TOURNAMENT_ID", tournamentId));
        call(registerTournamentUrl, "{}", true)
                .exchangeToMono(this::checkResponse)
                .retryWhen(this.retry)
                .block();

    }

    public RestResponse placeBattleship(String gameId, int x, int y, String direction) {
        getAccessToken();
        String callPayload = PLACE_SHIP_PAYLOAD
                .replaceAll("INPUT_GAME_ID", gameId)
                .replaceAll("INPUT_X", String.valueOf(x))
                .replaceAll("INPUT_Y", String.valueOf(y))
                .replaceAll("INPUT_DIRECTION", direction.toUpperCase());
        final String placeBattleshipUrl = baseUrl.concat(PLACE_BATTLESHIP_URL_SUFFIX.replaceAll("TOURNAMENT_ID", tournamentId));
        return call(placeBattleshipUrl, callPayload, true)
                .exchangeToMono(this::checkResponse)
                .retryWhen(this.retry).block();
    }

    private RequestHeadersSpec<?> call(String url, String body, boolean withToken) {
        return uriSpec
                .uri(url)
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    if (withToken) {
                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                    }
                });
    }

    private Mono<RestResponse> checkResponse(ClientResponse response) {
        if (!response.headers().contentType().orElse(MediaType.APPLICATION_XML).equals(MediaType.APPLICATION_JSON)) {
            return response.bodyToMono(String.class).map(s -> new RestResponse(response.rawStatusCode(), s, "", 0));
        }
        if (response.statusCode().equals(HttpStatus.OK)
                || response.statusCode().equals(HttpStatus.BAD_REQUEST)
                || response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            return response.bodyToMono(RestResponse.class);
        } else {
            return Mono.just(new RestResponse(response.rawStatusCode(), "Uknown error", "", 0));
        }

    }

    private Mono<AuthResponse> checkResponseAuth(ClientResponse response) {
        if (response.statusCode().equals(HttpStatus.OK)) {
            return response.bodyToMono(AuthResponse.class);
        } else {
            return Mono.just(new AuthResponse(""));
        }
    }

}
