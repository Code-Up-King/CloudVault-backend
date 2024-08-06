package org.chad.cloudvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chad.cloudvault.domain.po.FileInfo;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
}
