package com.macro.mall.portal.service;

import com.macro.mall.model.UmsIntegrationChangeHistory;

public interface UmsIntegrationChangeHistoryService {
    void addIntegrationChangeHistory(Long memberId, Integer changeType, Integer count, Integer sourceType, String note);
}
