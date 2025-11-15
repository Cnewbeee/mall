package com.macro.mall.portal.controller;

import com.github.pagehelper.PageInfo;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberCheckin;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.UmsMemberCheckinService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

/**
 * 会员签到控制器
 */
@Controller
@Api(tags = "UmsMemberCheckinController")
@Tag(name = "UmsMemberCheckinController", description = "会员签到管理")
@RequestMapping("/member/checkin")
public class UmsMemberCheckinController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberCheckinController.class);

    @Autowired
    private UmsMemberCheckinService checkinService;

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("会员签到")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult checkin(Principal principal) {
        if (principal == null) {
            LOGGER.warn("Checkin: Principal is null");
            return CommonResult.unauthorized(null);
        }

        try {
            LOGGER.info("Checkin: Principal name: {}", principal.getName());

            // 直接从Principal获取用户名，然后通过memberService获取会员ID
            String username = principal.getName();
            Long memberId = memberService.getByUsername(username).getId();

            if (memberId == null) {
                LOGGER.warn("Checkin: Member ID is null for username: {}", username);
                return CommonResult.unauthorized(null);
            }

            LOGGER.info("Checkin: Processing checkin for member ID: {}", memberId);
            boolean success = checkinService.checkin(memberId);
            if (!success) {
                return CommonResult.failed("今日已签到");
            }

            // 获取连续签到天数
            Integer continuousDays = checkinService.getContinuousDays(memberId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("integration", (continuousDays >= 7 ? 20 : 10));
            result.put("continuousDays", continuousDays);

            return CommonResult.success(result);
        } catch (Exception e) {
            LOGGER.error("Checkin error: {}", e.getMessage(), e);
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation("会员补签")
    @PostMapping("/retroactive")
    @ResponseBody
    public CommonResult retroactiveCheckin(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date targetDate,
            Principal principal) {
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }

        try {
            // 1. 获取当前用户
            String username = principal.getName();
            UmsMember member = memberService.getByUsername(username);
            if (member == null) {
                return CommonResult.unauthorized("用户不存在");
            }

            // 2. 校验积分是否足够
            if (member.getIntegration() < 100) {
                return CommonResult.failed("积分不足，补签需要100积分");
            }

            // 3. 执行补签
            boolean success = checkinService.retroactiveCheckin(member.getId(), targetDate);
            if (!success) {
                return CommonResult.failed("该日期已签到或补签失败");
            }

            // 4. 扣除积分
            memberService.updateIntegration(member.getId(), member.getIntegration() - 100);

            // 5. 返回最新连续签到天数
            Integer continuousDays = checkinService.getContinuousDays(member.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("continuousDays", continuousDays);
            result.put("remainingIntegration", member.getIntegration() - 100);
            return CommonResult.success(result);

        } catch (Exception e) {
            LOGGER.error("补签失败: {}", e.getMessage(), e);
            return CommonResult.failed("系统异常");
        }
    }


    @ApiOperation("获取月份签到记录")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberCheckin>> getCheckinHistoryMonth(
            @RequestParam(value = "year") Integer year,
            @RequestParam(value = "month") Integer month,
            @RequestParam(value = "memberId", required = false) Long memberId) {
        try {
            if (memberId == null) {
                memberId = getCurrentMemberId();
            }
            if (memberId == null) {
                return CommonResult.unauthorized(null);
            }
            Date monthDate = getMonthFirstDay(year, month);
            List<UmsMemberCheckin> checkinList = checkinService.getCheckinHistoryByMonth(memberId, monthDate);
            System.out.println(CommonPage.restPage(checkinList));
            return CommonResult.success(CommonPage.restPage(checkinList));
        } catch (Exception e) {
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation("获取签到记录")
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberCheckin>> getCheckinHistory(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "7") Integer pageSize) {
        try {
            Long memberId = getCurrentMemberId();
            if (memberId == null) {
                return CommonResult.unauthorized(null);
            }

            List<UmsMemberCheckin> checkinList = checkinService.getCheckinHistory(memberId, pageNum, pageSize);
            return CommonResult.success(CommonPage.restPage(checkinList));
        } catch (Exception e) {
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation("查询当前连续签到天数")
    @RequestMapping(value = "/continuous", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getContinuousDays() {
        try {
            Long memberId = getCurrentMemberId();
            if (memberId == null) {
                return CommonResult.unauthorized(null);
            }

            Integer continuousDays = checkinService.getContinuousDays(memberId);
            Map<String, Object> result = new HashMap<>();
            result.put("continuousDays", continuousDays);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.unauthorized(null);
        }
    }

    @ApiOperation("检查今日是否已签到")
    @RequestMapping(value = "/today", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult checkToday(Principal principal) {
        Date today = new Date();
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }

        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            return CommonResult.unauthorized(null);
        }

        boolean checkedToday = checkinService.isChecked(memberId, today);
        System.out.println(checkedToday);
        Map<String, Object> result = new HashMap<>();
        result.put("checkedToday", checkedToday);
        return CommonResult.success(result);
    }


    /**
     * 获取当前登录会员ID
     */
    private Long getCurrentMemberId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                LOGGER.warn("Authentication is null");
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal == null) {
                LOGGER.warn("Authentication principal is null");
                return null;
            }

            LOGGER.info("Authentication principal class: {}", principal.getClass().getName());
            LOGGER.info("Authentication principal: {}", principal);

            if (principal instanceof MemberDetails) {
                MemberDetails memberDetails = (MemberDetails) principal;
                if (memberDetails.getUmsMember() != null) {
                    return memberDetails.getUmsMember().getId();
                } else {
                    LOGGER.warn("MemberDetails.getUmsMember() is null");
                    return null;
                }
            } else {
                LOGGER.warn("Authentication principal is not MemberDetails, actual type: {}", principal.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Error getting current member ID: {}", e.getMessage(), e);
            return null;
        }
    }

    private static Date getMonthFirstDay(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}