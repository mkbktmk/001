package com.campus.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_news")
public class News {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String summary;
    private String category;
    private String coverImage;
    private Long authorId;
    private String authorName;
    private Integer viewCount;
    private String status;
    private Integer isTop;
    private LocalDateTime publishTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
