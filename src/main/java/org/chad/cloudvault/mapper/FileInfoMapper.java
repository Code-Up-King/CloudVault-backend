package org.chad.cloudvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.chad.cloudvault.domain.po.FileInfo;

import java.util.List;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    @Select("SELECT * FROM t_file_info WHERE file_name LIKE CONCAT('%',#{name},'%') AND del_flag = 0")
    List<FileInfo> searchFilesByName(String name);
}
