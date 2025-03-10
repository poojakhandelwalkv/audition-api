package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The AuditionPostIntegrationClient class is a Spring component responsible for integrating with external systems or
 * services related to audition posts.
 */
@Component
@NoArgsConstructor
public class AuditionPostIntegrationClient {


    @Autowired
    private RestTemplate restTemplate;

    private static final String POSTS_ENDPOINT = "https://jsonplaceholder.typicode.com/posts";
    private static final String RESOURCE_NOT_FOUND = "Resource Not Found";

    /**
     * Retrieves a list of audition posts based on the provided query parameters. This method makes a RestTemplate call
     * to get posts from https://jsonplaceholder.typicode.com/posts
     *
     * @param queryParams A map of query parameters where the key is the parameter name.
     * @return List of AuditionPost.
     * @throws IllegalArgumentException If the No post is available.
     */
    public List<AuditionPost> getPosts(final Map<String, Object> queryParams) {

        try {
            final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(POSTS_ENDPOINT);
            Optional.ofNullable(queryParams)
                .ifPresent(params -> params.forEach((key, value) -> builder.queryParam(key, value)));

            final URI uri = builder.build().toUri();
            final ResponseEntity<AuditionPost[]> responseEntity = restTemplate.getForEntity(uri, AuditionPost[].class);
            final AuditionPost[] auditionPost = Optional.ofNullable(responseEntity.getBody()).orElseThrow(
                () -> new SystemException("No post available", RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND.value()));

            return List.of(auditionPost);

        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("No post available", RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND.value(), e);
            } else {
                throw new SystemException(e.getMessage(), "System error ", e.getStatusCode().value(), e);
            }

        }

    }


    /**
     * Retrieves audition posts based on the postId. This method makes a RestTemplate call to get posts from
     * https://jsonplaceholder.typicode.com/posts
     *
     * @param id : String
     * @return AuditionPost
     * @throws SystemException If Resource not found with id.
     */
    public AuditionPost getPostById(final String id) {

        try {
            final ResponseEntity<AuditionPost> responseEntity = restTemplate.getForEntity(POSTS_ENDPOINT + "/{id}",
                AuditionPost.class, id);
            return responseEntity.getBody();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with given id " + id, RESOURCE_NOT_FOUND,
                    HttpStatus.NOT_FOUND.value(), e);
            } else {
                throw new SystemException(e.getMessage(), "System error ", e.getStatusCode().value(), e);
            }
        }
    }


}
