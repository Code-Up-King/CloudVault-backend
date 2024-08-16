package org.chad.cloudvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chad.cloudvault.domain.po.FileShare;

@Mapper
public interface FileShareMapper extends BaseMapper<FileShare> {
}
