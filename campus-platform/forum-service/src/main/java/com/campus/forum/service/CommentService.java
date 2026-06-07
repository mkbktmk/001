package com.campus.forum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.forum.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /** 发表回复 */
    Comment reply(Long postId, Long parentId, String content, Long authorId, String authorName);

    /** 查看帖子的所有回复（楼中楼嵌套） */
    List<Comment> getCommentsByPostId(Long postId);

    /** 删除回复 */
    void deleteComment(Long commentId, Long userId);
}
