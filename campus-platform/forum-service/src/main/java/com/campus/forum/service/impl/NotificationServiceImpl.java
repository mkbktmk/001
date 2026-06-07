package com.campus.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.forum.entity.Notification;
import com.campus.forum.mapper.NotificationMapper;
import com.campus.forum.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void send(Long userId, String type, String title, String content, Long relatedId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content != null ? content : "");
        n.setRelatedId(relatedId);
        n.setIsRead(0);
        save(n);
    }

    @Override
    public Page<Notification> listByUser(int pageNum, int pageSize, Long userId) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime));
    }

    @Override
    public int unreadCount(Long userId) {
        return (int) count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
    }

    @Override
    public void markRead(Long id, Long userId) {
        Notification n = getById(id);
        if (n == null || !n.getUserId().equals(userId)) {
            throw new BizException(404, "通知不存在");
        }
        n.setIsRead(1);
        updateById(n);
    }
}
