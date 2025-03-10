package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPostComment;
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
 * The {@code AuditionCommentIntegrationClient} class is a Spring component responsible for integrating with external
 * systems or services related to audition posts comments.
 */
@Component
@NoArgsConstructor
public class AuditionCommentIntegrationClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String POSTS_ENDPOINT = "https://jsonplaceholder.typicode.com/posts";
    private static final String COMMENTS_ENDPOINT = "https://jsonplaceholder.typicode.com/comments";
    private static final String RESOURCE_NOT_FOUND = "Resource Not Found";

    /**
     * Retrieves a list of comments for a specific audition post. This method makes a RestTemplate call to get comments
     * for posts from https://jsonplaceholder.typicode.com/comments
     *
     * @param postId : String
     * @return List of AuditionPostComment
     * @throws IllegalArgumentException If no comments with Post id is available.
     */
    public List<AuditionPostComment> getCommentsForPost(final Integer postId) {
        try {
            final ResponseEntity<AuditionPostComment[]> responseEntity = restTemplate.getForEntity(
                POSTS_ENDPOINT + "/{id}/comments", AuditionPostComment[].class, postId);
            final AuditionPostComment[] auditionPostComment = responseEntity.getBody();

            if ((auditionPostComment != null ? auditionPostComment.length : 0) > 0) {
                return List.of(auditionPostComment);
            } else {
                throw new SystemException("Cannot find comments with Post id " + postId, RESOURCE_NOT_FOUND,
                    HttpStatus.NOT_FOUND.value());
            }

        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find comments with Post id " + postId, RESOURCE_NOT_FOUND,
                    HttpStatus.NOT_FOUND.value(), e);
            } else {
                throw new SystemException(e.getMessage(), "System Error", e.getStatusCode().value(),
                    e);
            }
        }
    }

    /**
     * Retrieves a list of audition posts comments based on the provided query parameters.
     *
     * @param queryParams A map of query parameters where the key is the parameter name.
     * @return List of AuditionPostComment.
     * @throws SystemException If Resource not found with query params.
     */
    public List<AuditionPostComment> getComments(final Map<String, Object> queryParams) {
        try {
            final URI uri = buildUriWithQueryParams(COMMENTS_ENDPOINT, queryParams);
            final ResponseEntity<AuditionPostComment[]> responseEntity = restTemplate.getForEntity(uri,
                AuditionPostComment[].class);
            final AuditionPostComment[] auditionPostComment = responseEntity.getBody();
            if (auditionPostComment != null && auditionPostComment.length > 0) {
                return List.of(auditionPostComment);
            } else {
                throw new SystemException("Cannot find comments with Post id ", RESOURCE_NOT_FOUND,
                    HttpStatus.NOT_FOUND.value());
            }

        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find comments ", RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND.value(), e);
            } else {
                throw new SystemException(e.getMessage(), "System Error", e.getStatusCode().value(),
                    e);
            }
        }
    }


    private URI buildUriWithQueryParams(final String baseUrl, final Map<String, Object> queryParams) {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        Optional.ofNullable(queryParams)
            .ifPresent(params -> params.forEach((key, value) -> builder.queryParam(key, value)));
        return builder.build().toUri();
    }

}
