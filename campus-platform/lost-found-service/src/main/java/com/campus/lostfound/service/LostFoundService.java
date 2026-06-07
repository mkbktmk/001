package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.entity.LostFound;

public interface LostFoundService extends IService<LostFound> {

    Page<LostFound> pageList(int pageNum, int pageSize, String type, String category, String keyword);

    LostFound getDetail(Long id);

    void publish(LostFound entity, Long userId, String userName);

    void update(LostFound entity, Long userId);

    void delete(Long id, Long userId);

    void markFound(Long id, Long userId);

    Page<LostFound> myPage(int pageNum, int pageSize, Long userId);
}
