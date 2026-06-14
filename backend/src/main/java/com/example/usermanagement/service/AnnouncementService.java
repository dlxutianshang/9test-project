package com.example.usermanagement.service;

import com.example.usermanagement.common.PageResult;
import com.example.usermanagement.dto.AnnouncementQueryDTO;
import com.example.usermanagement.entity.Announcement;

import java.util.List;

public interface AnnouncementService {

    PageResult<Announcement> getAnnouncementPage(AnnouncementQueryDTO queryDTO);

    Announcement getById(Long id);

    Announcement createAnnouncement(Announcement announcement);

    Announcement updateAnnouncement(Announcement announcement);

    void deleteAnnouncement(Long id);

    void deleteAnnouncements(List<Long> ids);

    boolean hasReadUsers(Long id);

    List<String> getReadUsers(Long id);
}
