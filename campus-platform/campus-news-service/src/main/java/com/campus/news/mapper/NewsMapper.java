package com.campus.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.news.entity.News;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper extends BaseMapper<News> {
}
