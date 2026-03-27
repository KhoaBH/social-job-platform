package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostCommentRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.Post;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostComment;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostCommentRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
public class PostCommentService {
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;
    public List<PostComment> getAll() {
        return postCommentRepository.findAll();
    }
    
    public PostComment getById(UUID id) {
        return postCommentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));
    }
    
    public List<PostComment> getByPostId(UUID postId) {
        return postCommentRepository.findByPostId(postId);
    }
    
    public List<PostComment> getRepliesByCommentId(UUID parentCommentId) {
        return postCommentRepository.findRepliesByCommentId(parentCommentId);
    }
    
    public PostComment create(UUID userId, UUID postId, PostCommentRequest data) {
        PostComment comment = new PostComment();
        Post post = postService.getById(postId);
        User user = userService.getById(userId);

        if (data.getPostId() != null && !data.getPostId().equals(postId)) {
            throw new IllegalArgumentException("Post ID in body does not match URL");
        }

        PostComment parentComment = null;
        if (data.getParentCommentId() != null) {
            parentComment = getById(data.getParentCommentId());
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("Parent comment does not belong to this post");
            }
        }

        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(data.getContent().trim());
        comment.setParentComment(parentComment);
        
        PostComment saved = postCommentRepository.save(comment);
        // postService.incrementCommentCount(postId);
        
        return saved;
    }
    
    public PostComment update(UUID id, UUID userId, PostCommentRequest data) {
        PostComment comment = getById(id);
        validateCommentOwner(comment, userId);

        if (data.getPostId() != null && !data.getPostId().equals(comment.getPost().getId())) {
            throw new IllegalArgumentException("Post ID cannot be changed");
        }

        UUID currentParentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
        if (data.getParentCommentId() != null && !data.getParentCommentId().equals(currentParentId)) {
            throw new IllegalArgumentException("Parent comment cannot be changed");
        }

        comment.setContent(data.getContent().trim());
        
        return postCommentRepository.save(comment);
    }
    
    public void delete(UUID id, UUID userId) {
        PostComment comment = getById(id);
        validateCommentOwner(comment, userId);
        comment.setDeleted(true);
        postCommentRepository.save(comment);
    }

    private void validateCommentOwner(PostComment comment, UUID userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only modify your own comments");
        }
    }
}
