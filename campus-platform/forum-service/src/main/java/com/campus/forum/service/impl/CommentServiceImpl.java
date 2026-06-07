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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final PostMapper postMapper;
    private final NotificationService notificationService;
    private final DataSource dataSource;

    @Override
    @Transactional
    public Comment reply(Long postId, Long parentId, String content, Long authorId, String authorName) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) throw new BizException(404, "帖子不存在或已删除");

        // 检查禁言
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        var rows = jdbc.queryForList("SELECT muted_until, TIMESTAMPDIFF(MINUTE, NOW(), muted_until) AS remain FROM t_user_mute WHERE user_id = ? AND muted_until > NOW()", authorId);
        if (rows != null && !rows.isEmpty()) {
            Long remain = ((Number)rows.get(0).get("remain")).longValue();
            String msg = "你已被禁言";
            if (remain < 60) msg += "，剩余 " + remain + " 分钟";
            else if (remain < 1440) msg += "，剩余 " + (remain/60) + " 小时 " + (remain%60) + " 分钟";
            else if (remain < 525600) msg += "，剩余 " + (remain/1440) + " 天";
            else msg += "，剩余 " + (remain/525600) + " 年";
            msg += "后解除";
            throw new BizException(403, msg);
        }

        Comment comment = new Comment();
        comment.setPostId(postId); comment.setParentId(parentId != null ? parentId : 0);
        comment.setContent(content); comment.setAuthorId(authorId);
        comment.setAuthorName(authorName); comment.setStatus(1);
        save(comment);

        post.setCommentCount(post.getCommentCount() + 1); postMapper.updateById(post);

        if (!post.getAuthorId().equals(authorId)) {
            notificationService.send(post.getAuthorId(), "comment_reply",
                "有人回复了你的帖子", authorName + " 评论了你的帖子", postId);
        }
        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return list(new LambdaQueryWrapper<Comment>().eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1).orderByAsc(Comment::getCreateTime));
    }

    @Override
    public void deleteComment(Long commentId, Long userId, String role) {
        Comment comment = getById(commentId);
        if (comment == null) throw new BizException(404, "回复不存在");
        if (!comment.getAuthorId().equals(userId) && !"admin".equals(role))
            throw new BizException(403, "只能删除自己的回复");
        comment.setStatus(0); updateById(comment);
    }

    @Override
    public void muteUser(Long userId, int minutes, Long adminId) {
        if (minutes < 1) minutes = 1;
        if (minutes > 5256000) minutes = 5256000;
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("INSERT INTO t_user_mute (user_id, muted_until, reason) VALUES (?, DATE_ADD(NOW(), INTERVAL ? MINUTE), '管理员禁言') ON DUPLICATE KEY UPDATE muted_until = DATE_ADD(NOW(), INTERVAL ? MINUTE)",
            userId, minutes, minutes);
    }

    @Override
    public boolean isUserMuted(Long userId) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long cnt = jdbc.queryForObject("SELECT COUNT(*) FROM t_user_mute WHERE user_id = ? AND muted_until > NOW()", Long.class, userId);
        return cnt != null && cnt > 0;
    }

    @Override
    public void unmuteUser(Long userId) {
        new JdbcTemplate(dataSource).update("DELETE FROM t_user_mute WHERE user_id = ?", userId);
    }
}
