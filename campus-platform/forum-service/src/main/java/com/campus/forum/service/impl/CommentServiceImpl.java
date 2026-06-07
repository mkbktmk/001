package com.campus.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.forum.entity.Comment;
import com.campus.forum.entity.Post;
import com.campus.forum.mapper.CommentMapper;
import com.campus.forum.mapper.PostMapper;
import com.campus.forum.service.CommentService;
import com.campus.forum.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final PostMapper postMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Comment reply(Long postId, Long parentId, String content, Long authorId, String authorName) {
        // 校验帖子存在
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) {
            throw new BizException(404, "帖子不存在或已删除");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setParentId(parentId != null ? parentId : 0);
        comment.setContent(content);
        comment.setAuthorId(authorId);
        comment.setAuthorName(authorName);
        comment.setStatus(1);
        save(comment);

        // 更新帖子回复数
        post.setCommentCount(post.getCommentCount() + 1);
        postMapper.updateById(post);

        // 通知帖主（自己评论自己时不通知）
        if (!post.getAuthorId().equals(authorId)) {
            notificationService.send(post.getAuthorId(), "comment_reply",
                    "有人回复了你的帖子",
                    authorName + " 评论：「" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + "」",
                    postId);
        }

        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getById(commentId);
        if (comment == null) {
            throw new BizException(404, "回复不存在");
        }
        if (!comment.getAuthorId().equals(userId)) {
            throw new BizException(403, "只能删除自己的回复");
        }
        comment.setStatus(0);
        updateById(comment);
    }
}
