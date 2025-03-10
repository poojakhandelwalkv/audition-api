package com.audition.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionCommentIntegrationClient;
import com.audition.integration.AuditionPostIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@NoArgsConstructor
class AuditionServiceTest {

    @Mock
    private AuditionPostIntegrationClient auditionPostIntegrationClient;

    @Mock
    private AuditionCommentIntegrationClient auditionCommentIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    private static final AuditionPost POST_1 = AuditionPost.builder().userId(1).id(1).title("Mock title1")
        .body("Mock description body1")
        .build();
    private static final AuditionPost POST_2 = AuditionPost.builder().userId(1).id(1).title("Mock title2")
        .body("Mock description body2")
        .build();

    @Test
    void testGetPostsById() {
        when(auditionPostIntegrationClient.getPostById("1")).thenReturn(POST_1);
        final AuditionPost posts = auditionService.getPostById("1");
        assertThat(posts).isNotNull();
        assertThat(posts.getUserId()).isEqualTo(1);
    }

    @Test
    void testGetPosts() {

        final Map<String, Object> queryParam = new ConcurrentHashMap<>();
        when(auditionPostIntegrationClient.getPosts(queryParam)).thenReturn(List.of(POST_1, POST_2));
        final List<AuditionPost> postsList = auditionService.getPosts(queryParam);
        assertThat(postsList).isNotNull();
        assertThat(postsList.size()).isEqualTo(2);
    }

    @Test
    void testGetCommentsByPostId() {
        final AuditionPostComment postComment1 = AuditionPostComment.builder().postId(1).id(1).name("Post comments")
            .email("mock@mock3.com")
            .body("comment by post ID").build();
        when(auditionCommentIntegrationClient.getCommentsForPost(1)).thenReturn(List.of(postComment1));
        final List<AuditionPostComment> list = auditionService.getCommentsForPost(1);
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getPostId()).isEqualTo(1);
    }

    @Test
    void testGetCommentsForPostId() {
        final AuditionPostComment postComment1 = AuditionPostComment.builder().postId(1).id(1).name("Filter comments")
            .email("mock@mock.com").body(
                "all Comments on the basis of filteration ").build();
        final Map<String, Object> queryParams = new ConcurrentHashMap<>();
        queryParams.put("postId", 1);
        when(auditionCommentIntegrationClient.getComments(queryParams)).thenReturn(List.of(postComment1));
        final List<AuditionPostComment> list = auditionService.getComments(queryParams);
        assertThat(list).isNotNull();
    }
}
