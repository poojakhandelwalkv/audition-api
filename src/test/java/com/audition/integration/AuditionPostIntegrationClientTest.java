package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@NoArgsConstructor
class AuditionPostIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionPostIntegrationClient auditionPostIntegrationClient;

    public static final AuditionPost POST_1 = AuditionPost.builder().userId(1).id(1).title("Mock title1")
        .body("Mock description body1").build();
    public static final AuditionPost POST_2 = AuditionPost.builder().userId(1).id(2).title("Mock title2")
        .body("Mock description body2").build();

    public static final String POST_BY_ID_ENDPOINT = "https://jsonplaceholder.typicode.com/posts/{id}";

    @Test
    void testGetPosts() {
        final AuditionPost[] posts = {POST_1, POST_2};
        final Map<String, Object> queryParam = new ConcurrentHashMap<>();
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPost[].class))).thenReturn(
            new ResponseEntity<>(posts, HttpStatus.OK));
        final List<AuditionPost> postsList = auditionPostIntegrationClient.getPosts(queryParam);
        assertThat(postsList).isNotNull();
        assertThat(postsList.size()).isEqualTo(2);
    }

    @Test
    void testGetPostsNullResponseBodyReturnSystemException() {

        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPost[].class))).thenReturn(
            new ResponseEntity<>(HttpStatus.OK));

        assertThrows(SystemException.class, () -> auditionPostIntegrationClient.getPosts(new ConcurrentHashMap<>()));
    }

    @Test
    void testGetPostsForAUserIdNotFound() {
        final Map<String, Object> filters = new ConcurrentHashMap<>();
        filters.put("userId", 1);
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPost[].class))).thenThrow(
            new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertThrows(SystemException.class, () -> auditionPostIntegrationClient.getPosts(filters));
    }

    @Test
    void testGetPostsForAUserIdBadRequest() {
        final Map<String, Object> filters = new ConcurrentHashMap<>();
        filters.put("userId", 1);
        when(restTemplate.getForEntity(ArgumentMatchers.any(), eq(AuditionPost[].class))).thenThrow(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        final Throwable exception = assertThrows(SystemException.class,
            () -> auditionPostIntegrationClient.getPosts(filters));
        assertTrue(exception.getMessage().contains("400 BAD_REQUEST"));
    }

    @Test
    void testGetPostsById() {

        when(restTemplate.getForEntity(POST_BY_ID_ENDPOINT, AuditionPost.class, "1")).thenReturn(
            new ResponseEntity<>(POST_1, HttpStatus.OK));
        final AuditionPost post = auditionPostIntegrationClient.getPostById("1");
        assertThat(post).isNotNull();
        assertEquals(post.getTitle(), POST_1.getTitle());
        assertEquals(post.getId(), POST_1.getId());
        assertEquals(post.getBody(), POST_1.getBody());
        assertEquals(post.getUserId(), POST_1.getUserId());
    }

    @Test
    void testGetPostsByIdNotFound() {
        when(restTemplate.getForEntity(POST_BY_ID_ENDPOINT, AuditionPost.class, "1")).thenThrow(
            new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertThrows(SystemException.class, () -> auditionPostIntegrationClient.getPostById("1"));
    }

    @Test
    void testGetPostsByIdBadRequest() {
        when(restTemplate.getForEntity(POST_BY_ID_ENDPOINT, AuditionPost.class, "1")).thenThrow(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        assertThrows(SystemException.class, () -> auditionPostIntegrationClient.getPostById("1"));
    }


}
