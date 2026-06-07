package com.campus.complaint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.complaint.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
}
