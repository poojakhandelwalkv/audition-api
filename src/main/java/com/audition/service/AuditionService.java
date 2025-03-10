package com.audition.service;

import com.audition.integration.AuditionCommentIntegrationClient;
import com.audition.integration.AuditionPostIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.AuditionPostComment;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for handling the core logic for managing audition posts, such as creating, updating,
 * retrieving, and deleting audition posts. It interacts with the data layer (e.g., repositories) to fetch and persist
 * audition-related data.
 * </p>
 */
@Service
@NoArgsConstructor
public class AuditionService {

    @Autowired
    private AuditionPostIntegrationClient auditionPostIntegrationClient;

    @Autowired
    private AuditionCommentIntegrationClient auditionCommentIntegrationClient;

    /**
     * Retrieves a list of audition posts based on the provided query parameters.
     *
     * @param queryParams A map of query parameters where the key is the parameter name.
     * @return List of AuditionPost.
     */
    public List<AuditionPost> getPosts(final Map<String, Object> queryParams) {
        return auditionPostIntegrationClient.getPosts(queryParams);
    }

    /**
     * Retrieves audition posts based on the postId.
     *
     * @param postId : String
     * @return AuditionPost
     */
    public AuditionPost getPostById(final String postId) {
        return auditionPostIntegrationClient.getPostById(postId);
    }

    /**
     * Retrieves a list of comments for a specific audition post.
     *
     * @param postId : String
     * @return List of AuditionPostComment
     */
    public List<AuditionPostComment> getCommentsForPost(final Integer postId) {
        return auditionCommentIntegrationClient.getCommentsForPost(postId);
    }

    /**
     * Retrieves a list of audition posts comments based on the provided query parameters.
     *
     * @param filters A map of query parameters where the key is the parameter name.
     * @return List of AuditionPostComment.
     */
    public List<AuditionPostComment> getComments(final Map<String, Object> filters) {
        return auditionCommentIntegrationClient.getComments(filters);
    }
}
