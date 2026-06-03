package vn.edu.uit.socialjob.platform.modules.post.repository;

import java.util.UUID;

public interface PostMetricCountProjection {
    UUID getPostId();
    long getCount();
}
