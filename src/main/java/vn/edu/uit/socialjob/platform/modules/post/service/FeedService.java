package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.dto.FeedRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.Feed;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostRepository;
import vn.edu.uit.socialjob.platform.modules.post.repository.FeedRepository;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;

@Service
public class FeedService {
    
    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    
    public List<Feed> getAll() {
        return feedRepository.findAll();
    }
    
    public Feed getById(UUID id) {
        return feedRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Feed not found"));
    }
    
    public List<Feed> getByUserId(UUID userId) {
        return feedRepository.findByUserId(userId);
    }
    
    public List<Feed> getUnreadFeedByUserId(UUID userId) {
        return feedRepository.findUnreadFeedByUserId(userId);
    }
    
    public Feed create(UUID userId, UUID postId, FeedRequest data) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Feed feed = new Feed();
        feed.setUserId(userId);
        feed.setPostId(postId);
        feed.setFeedType(data.getFeedType());
        feed.setIsRead(false);
        
        return feedRepository.save(feed);
    }
    
    public Feed update(UUID id, FeedRequest data) {
        Feed feed = getById(id);
        feed.setFeedType(data.getFeedType());
        
        return feedRepository.save(feed);
    }
    
    public Feed markAsRead(UUID id) {
        Feed feed = getById(id);
        feed.setIsRead(true);
        
        return feedRepository.save(feed);
    }
    
    public void delete(UUID id) {
        Feed feed = getById(id);
        feed.setDeleted(true);
        feedRepository.save(feed);
    }
}
