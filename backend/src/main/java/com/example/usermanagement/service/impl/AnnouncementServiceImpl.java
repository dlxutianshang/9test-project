package com.example.usermanagement.service.impl;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.AnnouncementQueryDTO;
import com.example.usermanagement.entity.Announcement;
import com.example.usermanagement.mapper.AnnouncementMapper;
import com.example.usermanagement.service.AnnouncementService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public PageResult<Announcement> getAnnouncementPage(AnnouncementQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        List<Announcement> records = announcementMapper.selectPage(queryDTO, offset, queryDTO.getPageSize());
        long total = announcementMapper.selectCount(queryDTO);
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public Announcement getById(Long id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreator(SecurityContextHolder.getContext().getAuthentication().getName());
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    public Announcement updateAnnouncement(Announcement announcement) {
        announcement.setUpdateTime(LocalDateTime.now());
        announcementMapper.update(announcement);
        return announcement;
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    @Override
    public List<String> getReadUsers(Long id) {
        return announcementMapper.selectReadUsers(id);
    }
}
