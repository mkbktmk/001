package com.campus.complaint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.complaint.entity.Complaint;
import com.campus.complaint.mapper.ComplaintMapper;
import com.campus.complaint.service.ComplaintService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    @Override
    public Page<Complaint> myPage(int pageNum, int pageSize, Long userId, String status) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<Complaint>()
                .eq(Complaint::getUserId, userId)
                .eq(status != null && !status.isBlank(), Complaint::getStatus, status)
                .orderByDesc(Complaint::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Page<Complaint> adminPage(int pageNum, int pageSize, String type, String status) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<Complaint>()
                .eq(type != null && !type.isBlank(), Complaint::getType, type)
                .eq(status != null && !status.isBlank(), Complaint::getStatus, status)
                .orderByAsc(Complaint::getStatus)   // 待处理优先
                .orderByDesc(Complaint::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void submit(Complaint complaint, Long userId, String userName) {
        complaint.setUserId(userId);
        complaint.setUserName(userName);
        complaint.setStatus("pending");
        save(complaint);
    }

    @Override
    public void handle(Long id, String status, String reply, Long handlerId, String handlerName) {
        Complaint complaint = getById(id);
        if (complaint == null) {
            throw new BizException(404, "工单不存在");
        }
        complaint.setStatus(status);
        complaint.setReply(reply);
        complaint.setHandlerId(handlerId);
        complaint.setHandlerName(handlerName);
        if ("done".equals(status)) {
            complaint.setResolveTime(LocalDateTime.now());
        }
        updateById(complaint);
    }

    @Override
    public void rate(Long id, Long userId, int rating, String feedback) {
        Complaint complaint = getById(id);
        if (complaint == null) {
            throw new BizException(404, "工单不存在");
        }
        if (!complaint.getUserId().equals(userId)) {
            throw new BizException(403, "只能评价自己的工单");
        }
        if (!"done".equals(complaint.getStatus())) {
            throw new BizException(400, "只能评价已完成的工单");
        }
        complaint.setRating(rating);
        complaint.setFeedback(feedback);
        updateById(complaint);
    }
}
