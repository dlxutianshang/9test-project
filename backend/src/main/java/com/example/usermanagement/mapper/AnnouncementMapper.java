package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.AnnouncementQueryDTO;
import com.example.usermanagement.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    List<Announcement> selectPage(@Param("query") AnnouncementQueryDTO queryDTO,
                                  @Param("offset") Integer offset,
                                  @Param("pageSize") Integer pageSize);

    long selectCount(@Param("query") AnnouncementQueryDTO queryDTO);

    Announcement selectById(@Param("id") Long id);

    int insert(Announcement announcement);

    int update(Announcement announcement);

    int deleteById(@Param("id") Long id);

    List<String> selectReadUsers(@Param("id") Long id);
}
