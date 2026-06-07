package com.campus.forum.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String images;       // JSON 数组

    private String board;

    private Long authorId;
    private String authorName;

    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    private Integer isPinned;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
