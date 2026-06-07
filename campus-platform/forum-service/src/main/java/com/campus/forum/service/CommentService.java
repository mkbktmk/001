package com.campus.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.forum.entity.Comment;
import java.util.List;

public interface CommentService extends IService<Comment> {
    Comment reply(Long postId, Long parentId, String content, Long authorId, String authorName);
    List<Comment> getCommentsByPostId(Long postId);
    void deleteComment(Long commentId, Long userId, String role);
    void muteUser(Long userId, int minutes, Long adminId);
    void unmuteUser(Long userId);
    boolean isUserMuted(Long userId);
}
