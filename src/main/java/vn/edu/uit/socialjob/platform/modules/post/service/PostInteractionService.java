package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostInteractionRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostInteraction;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostInteractionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostInteractionService {
    
    @Autowired
    private PostInteractionRepository postInteractionRepository;
    
    @Autowired
    private PostService postService;
    
    public List<PostInteraction> getAll() {
        return postInteractionRepository.findAll();
    }
    
    public PostInteraction getById(UUID id) {
        return postInteractionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post interaction not found"));
    }
    
    public List<PostInteraction> getByPostId(UUID postId) {
        return postInteractionRepository.findByPostId(postId);
    }
    
    public PostInteraction create(UUID postId, UUID userId, PostInteractionRequest data) {
        PostInteraction interaction = new PostInteraction();
        interaction.setPostId(postId);
        interaction.setUserId(userId);
        interaction.setInteractionType(data.getInteractionType());
        
        PostInteraction saved = postInteractionRepository.save(interaction);
        postService.incrementInteractionCount(postId);
        
        return saved;
    }
    
    public PostInteraction update(UUID id, PostInteractionRequest data) {
        PostInteraction interaction = getById(id);
        interaction.setInteractionType(data.getInteractionType());
        
        return postInteractionRepository.save(interaction);
    }
    
    public void delete(UUID id) {
        PostInteraction interaction = getById(id);
        interaction.setDeleted(true);
        postInteractionRepository.save(interaction);
        postService.decrementInteractionCount(interaction.getPostId());
    }
    
    public Optional<PostInteraction> getUserInteractionOnPost(UUID postId, UUID userId) {
        return postInteractionRepository.findByPostIdAndUserId(postId, userId);
    }
}
