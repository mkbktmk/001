package com.campus.forum.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;
    private Long parentId;
    private Long replyToId;
    private Long replyToUid;
    private String content;
    private Long authorId;
    private String authorName;
    private Integer likeCount;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
