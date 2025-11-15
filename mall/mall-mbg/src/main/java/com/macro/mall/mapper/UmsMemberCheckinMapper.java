package com.macro.mall.mapper;

import com.macro.mall.model.UmsMemberCheckin;
import com.macro.mall.model.UmsMemberCheckinExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsMemberCheckinMapper {
    long countByExample(UmsMemberCheckinExample example);

    int deleteByExample(UmsMemberCheckinExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberCheckin record);

    int insertSelective(UmsMemberCheckin record);

    List<UmsMemberCheckin> selectByExample(UmsMemberCheckinExample example);

    UmsMemberCheckin selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UmsMemberCheckin record, @Param("example") UmsMemberCheckinExample example);

    int updateByExample(@Param("record") UmsMemberCheckin record, @Param("example") UmsMemberCheckinExample example);

    int updateByPrimaryKeySelective(UmsMemberCheckin record);

    int updateByPrimaryKey(UmsMemberCheckin record);
}