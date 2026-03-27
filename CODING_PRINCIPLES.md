# Coding Principles and Consistency Guide

## 1. Purpose
This document defines coding principles for the backend to keep implementation consistent, secure, and maintainable across modules.

Scope:
- REST API contract design
- Authentication and authorization
- Foreign key validation
- Validation layering
- Error handling conventions
- DTO usage and service invariants

## 2. API Contract Principles
### 2.1 Single source of truth for identifiers
If an identifier is present in the URL path, treat it as the primary source of truth.

Rules:
- Prefer `postId` from path over body when creating comments for a post.
- If body also includes `postId`, verify it matches the path value.
- Reject mismatched input with `400 Bad Request`.

Example:
```java
public PostComment create(UUID userId, UUID postId, PostCommentRequest data) {
    if (data.getPostId() != null && !data.getPostId().equals(postId)) {
        throw new IllegalArgumentException("Post ID in body does not match URL");
    }
    Post post = postService.getById(postId);
    // continue create flow
}
```

### 2.2 Do not duplicate semantics in multiple request fields
Avoid request shapes where two fields represent the same meaning unless there is a clear backward-compatibility reason and strict consistency checks are enforced.

## 3. Security Principles
### 3.1 Separate authentication from authorization
- Authentication answers: Who is the caller?
- Authorization answers: Is the caller allowed to perform this action?

Both checks are mandatory for write operations.

Example:
```java
private void validateCommentOwner(PostComment comment, UUID userId) {
    if (!comment.getUser().getId().equals(userId)) {
        throw new IllegalArgumentException("You can only modify your own comments");
    }
}
```

### 3.2 Default-deny API policy
Apply `authenticated()` for `/api/**` by default, then explicitly open public endpoints.

Recommended public endpoints:
- `/api/auth/**`
- `POST /api/users/register`
- Swagger/OpenAPI docs
- `OPTIONS` preflight requests

## 4. Foreign Key and Relationship Principles
### 4.1 Validate existence before assigning relationships
Never assign relationship fields from unknown IDs without checking existence.

Avoid:
```java
entity.setCategory(repository.findById(id).orElse(null));
entity.setUser(userRepository.getReferenceById(userId));
```

Prefer:
```java
SkillCategory category = skillCategoryRepository.findById(categoryId)
    .orElseThrow(() -> new IllegalArgumentException("Skill category not found"));
skill.setCategory(category);
```

### 4.2 Validate relational consistency
When related objects are provided together, verify they belong to the same aggregate context.

Example:
- A reply comment must belong to the same post as the target post.

```java
PostComment parent = getById(data.getParentCommentId());
if (!parent.getPost().getId().equals(postId)) {
    throw new IllegalArgumentException("Parent comment does not belong to this post");
}
```

## 5. Validation Layering Principles
### 5.1 DTO validation for shape and format
Use Bean Validation annotations for field-level constraints.

Typical checks:
- `@NotBlank` for text content
- `@NotNull` for required IDs where appropriate

### 5.2 Service validation for business rules
Service layer must enforce business invariants that DTO annotations cannot represent.

Examples:
- Prevent self-follow
- Prevent duplicate pending friend request
- Prevent changing immutable associations in update operations

## 6. Error Handling Principles
### 6.1 Use status codes consistently
- `400 Bad Request`: invalid input or violated business constraints
- `401 Unauthorized`: missing/invalid authentication
- `403 Forbidden`: authenticated but not allowed
- `404 Not Found`: resource does not exist (optional policy depending on API style)
- `500 Internal Server Error`: unexpected server faults only

### 6.2 Prefer explicit, stable error messages
Error messages should clearly indicate which constraint failed.

Good:
- "Skill category not found"
- "Parent comment does not belong to this post"

Avoid:
- Generic null pointer or cast errors exposed to client

## 7. DTO Design Principles
### 7.1 One DTO per use case when semantics differ
If create and update rules differ significantly, use separate DTOs.

Recommended:
- `CreatePostCommentRequest`
- `UpdatePostCommentRequest`

### 7.2 Keep backward compatibility intentionally
If maintaining a legacy field, document it and enforce consistency in service logic.

## 8. Service Invariant Principles
Each service method should maintain a clear invariant.

Examples:
- `create`: all references are valid, all mandatory business checks passed.
- `update`: only mutable fields can change.
- `delete`: soft delete should preserve auditability and ownership rules.

## 9. Controller Principles
### 9.1 Keep controllers thin
Controller responsibilities:
- Parse and validate request at transport level
- Extract authenticated user ID
- Delegate business decisions to service

### 9.2 Do not duplicate business rules in controller
Ownership checks, relationship checks, and aggregate consistency should live in services.

## 10. Practical Checklist Before Merging
1. Security
- Is endpoint protection correct (`/api/**` default authenticated)?
- Is both authentication and authorization applied where needed?

2. Relationship safety
- Are all FK references validated before assignment?
- Are cross-object consistency checks present?

3. Validation and errors
- Are DTO annotations complete?
- Are service rules enforced with explicit messages?
- Are returned status codes consistent?

4. API contract
- Is source of truth for IDs unambiguous?
- If duplicated in body/path, is mismatch checked?

5. Maintainability
- Is method behavior predictable?
- Are update operations preventing immutable-field changes?

## 11. Example End-to-End Pattern (Post Comment)
```java
public PostComment create(UUID userId, UUID postId, PostCommentRequest data) {
    if (data.getPostId() != null && !data.getPostId().equals(postId)) {
        throw new IllegalArgumentException("Post ID in body does not match URL");
    }

    Post post = postService.getById(postId);
    User user = userService.getById(userId);

    PostComment parent = null;
    if (data.getParentCommentId() != null) {
        parent = getById(data.getParentCommentId());
        if (!parent.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Parent comment does not belong to this post");
        }
    }

    PostComment comment = new PostComment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setParentComment(parent);
    comment.setContent(data.getContent().trim());

    return postCommentRepository.save(comment);
}
```

---
This guide is intended to be a living document. Update it whenever new cross-cutting conventions are introduced.
