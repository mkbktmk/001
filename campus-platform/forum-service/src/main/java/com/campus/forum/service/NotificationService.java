package com.campus.forum.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.forum.entity.Notification;

public interface NotificationService extends IService<Notification> {

    /** 发送通知 */
    void send(Long userId, String type, String title, String content, Long relatedId);

    /** 用户通知列表 */
    Page<Notification> listByUser(int page, int size, Long userId);

    /** 未读数 */
    int unreadCount(Long userId);

    /** 标记已读 */
    void markRead(Long id, Long userId);
}
