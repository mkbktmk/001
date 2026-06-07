package com.campus.complaint.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_complaint")
public class Complaint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** repair / complaint / suggest */
    private String type;

    private String title;
    private String description;
    private String images;       // JSON
    private String location;

    /** pending / processing / done / rejected */
    private String status;

    private Long userId;
    private String userName;

    private Long handlerId;
    private String handlerName;
    private String reply;

    private Integer rating;     // 1-5
    private String feedback;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime resolveTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
