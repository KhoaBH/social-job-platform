package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostCommentRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostComment;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostCommentRepository;
import java.util.List;
import java.util.UUID;

@Service
public class PostCommentService {
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private PostService postService;
    
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
    
    public PostComment create(UUID postId, UUID userId, PostCommentRequest data) {
        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(data.getContent().trim());
        comment.setParentCommentId(data.getParentCommentId());
        
        PostComment saved = postCommentRepository.save(comment);
        postService.incrementCommentCount(postId);
        
        return saved;
    }
    
    public PostComment update(UUID id, PostCommentRequest data) {
        PostComment comment = getById(id);
        comment.setContent(data.getContent().trim());
        
        return postCommentRepository.save(comment);
    }
    
    public void delete(UUID id) {
        PostComment comment = getById(id);
        comment.setDeleted(true);
        postCommentRepository.save(comment);
        postService.decrementCommentCount(comment.getPostId());
    }
}
