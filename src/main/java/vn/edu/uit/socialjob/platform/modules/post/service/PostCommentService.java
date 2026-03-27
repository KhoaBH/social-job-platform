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
    
    public PostComment create(UUID userId, PostCommentRequest data) {
        PostComment comment = new PostComment();
        Post post = postService.getById(data.getPostId());
        User user = userService.getById(userId);
        if(data.getParentCommentId() != null) {
            getById(data.getParentCommentId()); 
            comment.setParentComment(getById(data.getParentCommentId()));
        }
        else {
            comment.setParentComment(null);
        }
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(data.getContent().trim());
        
        
        PostComment saved = postCommentRepository.save(comment);
        // postService.incrementCommentCount(postId);
        
        return saved;
    }
    
    public PostComment update(UUID id, PostCommentRequest data) {
        PostComment comment = getById(id);
        comment.setContent(data.getContent().trim());
        
        return postCommentRepository.save(comment);
    }
    
    // public void delete(UUID id) {
    //     PostComment comment = getById(id);
    //     comment.setDeleted(true);
    //     postCommentRepository.save(comment);
    //     postService.decrementCommentCount(comment.getPostId());
    // }
}
