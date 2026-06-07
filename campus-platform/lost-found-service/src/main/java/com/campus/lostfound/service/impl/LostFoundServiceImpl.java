package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.lostfound.entity.LostFound;
import com.campus.lostfound.mapper.LostFoundMapper;
import com.campus.lostfound.service.LostFoundService;
import org.springframework.stereotype.Service;

@Service
public class LostFoundServiceImpl extends ServiceImpl<LostFoundMapper, LostFound> implements LostFoundService {

    @Override
    public Page<LostFound> pageList(int pageNum, int pageSize, String type, String category, String keyword) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<LostFound>()
                        .eq(LostFound::getStatus, "active")
                        .eq(type != null && !type.isBlank(), LostFound::getType, type)
                        .eq(category != null && !category.isBlank(), LostFound::getCategory, category)
                        .and(keyword != null && !keyword.isBlank(), q -> q
                                .like(LostFound::getItemName, keyword)
                                .or()
                                .like(LostFound::getDescription, keyword))
                        .orderByDesc(LostFound::getCreateTime));
    }

    @Override
    public LostFound getDetail(Long id) {
        LostFound entity = getById(id);
        if (entity == null) throw new BizException(404, "启事不存在");
        entity.setViewCount(entity.getViewCount() + 1);
        updateById(entity);
        return entity;
    }

    @Override
    public void publish(LostFound entity, Long userId, String userName) {
        entity.setUserId(userId);
        entity.setUserName(userName);
        entity.setStatus("active");
        entity.setViewCount(0);
        save(entity);
    }

    @Override
    public void update(LostFound entity, Long userId) {
        LostFound exist = getById(entity.getId());
        if (exist == null) throw new BizException(404, "启事不存在");
        if (!exist.getUserId().equals(userId)) throw new BizException(403, "只能编辑自己的启事");
        exist.setType(entity.getType());
        exist.setItemName(entity.getItemName());
        exist.setCategory(entity.getCategory());
        exist.setDescription(entity.getDescription());
        exist.setLocation(entity.getLocation());
        exist.setContact(entity.getContact());
        if (entity.getImages() != null) exist.setImages(entity.getImages());
        updateById(exist);
    }

    @Override
    public void delete(Long id, Long userId) {
        LostFound entity = getById(id);
        if (entity == null) throw new BizException(404, "启事不存在");
        if (!entity.getUserId().equals(userId)) throw new BizException(403, "只能删除自己的启事");
        removeById(id);
    }

    @Override
    public Page<LostFound> myPage(int pageNum, int pageSize, Long userId) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<LostFound>()
                        .eq(LostFound::getUserId, userId)
                        .orderByDesc(LostFound::getCreateTime));
    }

    @Override
    public void markFound(Long id, Long userId) {
        LostFound entity = getById(id);
        if (entity == null) throw new BizException(404, "启事不存在");
        if (!entity.getUserId().equals(userId)) throw new BizException(403, "只能操作自己的启事");
        entity.setStatus("found");
        updateById(entity);
    }
}
