package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.portal.service.UmsIntegrationChangeHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class UmsIntegrationChangeHistoryServiceImpl implements UmsIntegrationChangeHistoryService {
    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;
    @Autowired
    private UmsMemberCacheServiceImpl memberCacheService;

    @Override
    public void addIntegrationChangeHistory(Long memberId, Integer changeType, Integer count, Integer sourceType, String note){
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(memberId);
        history.setCreateTime(new Date());
        history.setChangeType(changeType); // 0->增加
        history.setChangeCount(count);
        history.setSourceType(sourceType); // 2->签到
        history.setOperateMan("system");
        history.setOperateNote(note);
        integrationChangeHistoryMapper.insert(history);
        // 刷新缓存
        memberCacheService.delMember(memberId);
    }
}
