package com.audition.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPostComment;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@NoArgsConstructor
class AuditionCommentIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionCommentIntegrationClient auditionCommentIntegrationClient;


    public static final AuditionPostComment POST_COMMENT_1 = new AuditionPostComment(1, 1, "Mock title1",
        "moch@email.com",
        "Mock description comment1");
    public static final AuditionPostComment POST_COMMENT_2 = new AuditionPostComment(1, 2, "Mock title2",
        "moche@email.com",
        "Mock description comment2");
    public static final String POST_BY_ID_ENDPOINT = "https://jsonplaceholder.typicode.com/posts/{id}";

    public static final String COMMENT_POST_ID = "https://jsonplaceholder.typicode.com/posts/{id}/comments";


    @Test
    void testGetCommentsForPost() {

        final AuditionPostComment[] postComments = {POST_COMMENT_1, POST_COMMENT_2};
        final ResponseEntity re = new ResponseEntity<>(postComments, HttpStatus.OK);
        when(
            restTemplate.getForEntity(COMMENT_POST_ID,
                AuditionPostComment[].class,
                1))
            .thenReturn(re);
        final List<AuditionPostComment> commentsList = auditionCommentIntegrationClient.getCommentsForPost(1);
        assertThat(commentsList).isNotNull();
        assertThat(commentsList.size()).isEqualTo(2);
    }

    @Test
    void testGetCommentsForPostEmptyCommentsReturnSystemException() {

        final AuditionPostComment[] postComments = {};

        final ResponseEntity re = new ResponseEntity<>(postComments, HttpStatus.OK);
        when(
            restTemplate.getForEntity(COMMENT_POST_ID,
                AuditionPostComment[].class,
                1))
            .thenReturn(re);
        assertThrows(SystemException.class, () -> {
            auditionCommentIntegrationClient.getCommentsForPost(1);
        });
    }

    @Test
    void testGetCommentsForNullPostCommentsReturnSystemException() {

        final ResponseEntity re = new ResponseEntity<>(null, HttpStatus.OK);
        when(
            restTemplate.getForEntity(COMMENT_POST_ID,
                AuditionPostComment[].class,
                1))
            .thenReturn(re);
        assertThrows(SystemException.class, () -> {
            auditionCommentIntegrationClient.getCommentsForPost(1);
        });
    }

    @Test
    void testGetCommentsForPostPostIdNotFound() {
        when(restTemplate.getForEntity(COMMENT_POST_ID,
            AuditionPostComment[].class,
            1)).thenThrow(
            new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertThrows(SystemException.class, () -> {
            auditionCommentIntegrationClient.getCommentsForPost(1);
        });
    }

    @Test
    void testGetCommentsForPostBadRequest() {
        when(restTemplate.getForEntity(COMMENT_POST_ID,
            AuditionPostComment[].class,
            1)).thenThrow(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        assertThrows(SystemException.class, () -> {
            auditionCommentIntegrationClient.getCommentsForPost(1);
        });
    }

    @Test
    void testGetComments() {

        final AuditionPostComment[] postComments = {POST_COMMENT_1, POST_COMMENT_2};

        final Map<String, Object> queryParam = new ConcurrentHashMap<>();
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPostComment[].class))).thenReturn(
            new ResponseEntity<>(postComments,
                HttpStatus.OK));
        final List<AuditionPostComment> commentList = auditionCommentIntegrationClient.getComments(queryParam);
        assertThat(commentList).isNotNull();
        assertThat(commentList.size()).isEqualTo(2);
    }

    @Test
    void testGetCommentsEmptyResponseBodyReturnSystemException() {
        final AuditionPostComment[] postComments = {};
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPostComment[].class))).thenReturn(
            new ResponseEntity<>(postComments,
                HttpStatus.OK));

        assertThrows(SystemException.class,
            () -> auditionCommentIntegrationClient.getComments(new ConcurrentHashMap<>()));
    }

    @Test
    void testGetCommentsNullResponseBodyReturnSystemException() {
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPostComment[].class))).thenReturn(
            new ResponseEntity<>(null,
                HttpStatus.OK));

        assertThrows(SystemException.class,
            () -> auditionCommentIntegrationClient.getComments(new ConcurrentHashMap<>()));
    }

    @Test
    void testGetCommentsForAPostIdNotFound() {
        final Map<String, Object> filters = new ConcurrentHashMap<>();
        filters.put("postId", 1);
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPostComment[].class))).thenThrow(
            new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertThrows(SystemException.class, () -> auditionCommentIntegrationClient.getComments(filters));
    }

    @Test
    void testGetCommentsForPostIdBadRequest() {
        final Map<String, Object> filters = new ConcurrentHashMap<>();
        filters.put("postId", 1);
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPostComment[].class))).thenThrow(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        final Throwable exception = assertThrows(SystemException.class,
            () -> auditionCommentIntegrationClient.getComments(filters));
        assertTrue(exception.getMessage().contains("400 BAD_REQUEST"));
    }


}
