package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.entity.CommentInteraction;
import vn.edu.uit.socialjob.platform.modules.post.repository.CommentInteractionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentInteractionService {

    private final CommentInteractionRepository commentInteractionRepository;
    private final PostCommentService postCommentService;

    public CommentInteractionService(
        CommentInteractionRepository commentInteractionRepository,
        PostCommentService postCommentService
    ) {
        this.commentInteractionRepository = commentInteractionRepository;
        this.postCommentService = postCommentService;
    }

    public List<CommentInteraction> getAll() {
        return commentInteractionRepository.findAll();
    }

    public CommentInteraction getById(UUID id) {
        return commentInteractionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Comment interaction not found"));
    }

    public List<CommentInteraction> getByCommentId(UUID commentId) {
        return commentInteractionRepository.findByCommentId(commentId);
    }

    public Optional<CommentInteraction> getUserInteractionOnComment(UUID commentId, UUID userId) {
        return commentInteractionRepository.findByCommentIdAndUserId(commentId, userId);
    }

    public CommentInteraction like(UUID commentId, UUID userId) {
        postCommentService.getById(commentId);

        Optional<CommentInteraction> existing = commentInteractionRepository.findAnyByCommentIdAndUserId(commentId, userId);
        if (existing.isPresent()) {
            CommentInteraction interaction = existing.get();
            if (interaction.isDeleted()) {
                interaction.setDeleted(false);
                return commentInteractionRepository.save(interaction);
            }
            return interaction;
        }

        CommentInteraction interaction = new CommentInteraction();
        interaction.setCommentId(commentId);
        interaction.setUserId(userId);
        return commentInteractionRepository.save(interaction);
    }

    public void unlikeByComment(UUID commentId, UUID userId) {
        Optional<CommentInteraction> existing = commentInteractionRepository.findByCommentIdAndUserId(commentId, userId);
        if (existing.isEmpty()) {
            return;
        }

        CommentInteraction interaction = existing.get();
        interaction.setDeleted(true);
        commentInteractionRepository.save(interaction);
    }

    public void delete(UUID id, UUID userId) {
        CommentInteraction interaction = getById(id);
        ensureOwner(interaction, userId);
        interaction.setDeleted(true);
        commentInteractionRepository.save(interaction);
    }

    private void ensureOwner(CommentInteraction interaction, UUID userId) {
        if (!interaction.getUserId().equals(userId)) {
            throw new IllegalStateException("You are not allowed to modify this interaction");
        }
    }
}