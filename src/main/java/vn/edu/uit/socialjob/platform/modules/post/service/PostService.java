package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.Post;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostRepository;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    public List<Post> getAll() {
        return postRepository.findAll();
    }
    
    public Post getById(UUID id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
    
    public List<Post> getByUserId(UUID userId) {
        return postRepository.findByUserId(userId);
    }
    
    public Post create(UUID userId, PostRequest data) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(data.getTitle().trim());
        post.setDescription(data.getDescription());
        post.setContent(data.getContent());
        post.setInteractionCount(0);
        post.setCommentCount(0);
        
        return postRepository.save(post);
    }
    
    public Post update(UUID id, PostRequest data) {
        Post post = getById(id);
        post.setTitle(data.getTitle().trim());
        post.setDescription(data.getDescription());
        post.setContent(data.getContent());
        
        return postRepository.save(post);
    }
    
    public void delete(UUID id) {
        Post post = getById(id);
        post.setDeleted(true);
        postRepository.save(post);
    }
    
    public Post incrementInteractionCount(UUID postId) {
        Post post = getById(postId);
        post.setInteractionCount(post.getInteractionCount() + 1);
        return postRepository.save(post);
    }
    
    public Post decrementInteractionCount(UUID postId) {
        Post post = getById(postId);
        post.setInteractionCount(Math.max(0, post.getInteractionCount() - 1));
        return postRepository.save(post);
    }
    
    public Post incrementCommentCount(UUID postId) {
        Post post = getById(postId);
        post.setCommentCount(post.getCommentCount() + 1);
        return postRepository.save(post);
    }
    
    public Post decrementCommentCount(UUID postId) {
        Post post = getById(postId);
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        return postRepository.save(post);
    }
}
