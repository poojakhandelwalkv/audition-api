package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import com.audition.service.AuditionService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@NoArgsConstructor
@SpringBootTest
class AuditionControllerTest {

    @Mock
    private AuditionService auditionService;

    @InjectMocks
    private AuditionController auditionController;

    private static final AuditionPost POST_1 = AuditionPost.builder().userId(1).id(1).title("Mock title1")
        .body("Mock description body1")
        .build();
    private static final AuditionPost POST_2 = AuditionPost.builder().userId(1).id(1).title("Mock title2")
        .body("Mock description body2")
        .build();

    private static final AuditionPostComment POST_COMMENT_1 = AuditionPostComment.builder().postId(1).id(1)
        .name("Mock title")
        .email("mock@mock.com").body(
            "Mock description body").build();
    private static final AuditionPostComment POST_COMMENT_2 = AuditionPostComment.builder().postId(1).id(2)
        .name("Mock title")
        .email("mock@mock.com").body(
            "Mock description body").build();

    @Test
    void testGetPostsWithoutQueryParam() {

        when(auditionService.getPosts(new ConcurrentHashMap<>())).thenReturn(List.of(POST_1, POST_2));
        final List<AuditionPost> postsList = auditionController.getPosts(null, null);
        assertThat(postsList).isNotNull();
        assertThat(postsList.size()).isEqualTo(2);
        assertThat(postsList.get(0)).isEqualTo(POST_1);
        assertThat(postsList.get(1)).isEqualTo(POST_2);
    }


    @Test
    void testGetPostsWithUserIdParam() {
        final Map<String, Object> queryParam = new ConcurrentHashMap<>();
        queryParam.put("userId", 1);
        when(auditionService.getPosts(queryParam)).thenReturn(List.of(POST_1));
        final List<AuditionPost> postsList = auditionController.getPosts(1, null);
        assertThat(postsList).isNotNull();
        assertThat(postsList.size()).isEqualTo(1);
        assertThat(postsList.get(0)).isEqualTo(POST_1);
    }

    @Test
    void testGetPostsWithPostIdParam() {

        final Map<String, Object> queryParam = new ConcurrentHashMap<>();
        queryParam.put("id", 1);
        when(auditionService.getPosts(queryParam)).thenReturn(List.of(POST_1));
        final List<AuditionPost> postsList = auditionController.getPosts(null, 1);
        assertThat(postsList).isNotNull();
        assertThat(postsList.size()).isEqualTo(1);
        assertThat(postsList.get(0)).isEqualTo(POST_1);
    }

    @Test
    void testGetPostsById() {
        when(auditionService.getPostById("1")).thenReturn(POST_1);
        final AuditionPost actualPost = auditionController.getPosts("1");
        assertThat(actualPost).isNotNull();
        assertThat(actualPost.getId()).isEqualTo(POST_1.getId());
    }

    @Test
    void testGetPostsByInvalidId() {
        assertThrows(SystemException.class, () -> auditionController.getPosts("abc"));
    }

    @Test
    void testGetCommentsByPostId() {

        when(auditionService.getCommentsForPost(1)).thenReturn(List.of(POST_COMMENT_1, POST_COMMENT_2));
        final List<AuditionPostComment> list = auditionController.getCommentsForPost(1);
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void testGetCommentsByInvalidPostId() {
        assertThrows(SystemException.class, () -> auditionController.getCommentsForPost(0));
    }

    @Test
    void testGetComments() {
        final Map<String, Object> queryParam = new ConcurrentHashMap<>();
        queryParam.put("postId", 1);
        when(auditionService.getComments(queryParam)).thenReturn(List.of(POST_COMMENT_1, POST_COMMENT_2));
        final List<AuditionPostComment> list = auditionController.getComments(1);
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(2);
    }
}
