package com.campus.complaint.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.complaint.entity.Complaint;

public interface ComplaintService extends IService<Complaint> {

    /** 用户：查看自己的工单 */
    Page<Complaint> myPage(int pageNum, int pageSize, Long userId, String status);

    /** 管理员：查看全部工单 */
    Page<Complaint> adminPage(int pageNum, int pageSize, String type, String status);

    /** 提交工单 */
    void submit(Complaint complaint, Long userId, String userName);

    /** 管理员：处理工单 */
    void handle(Long id, String status, String reply, Long handlerId, String handlerName);

    /** 用户：评价工单 */
    void rate(Long id, Long userId, int rating, String feedback);
}
