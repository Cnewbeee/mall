package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsRole;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "UmsMemberController")
@Tag(name = "UmsMemberController", description = "会员管理")
@RequestMapping("/member")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;
    @ApiOperation("查询所有会员")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMember>> list(UmsMember member, Integer pageSize, Integer pageNum) {
        List<UmsMember> memberList = memberService.list(member, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(memberList));
    }
    @ApiOperation("添加会员")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody UmsMember role) {
        int count = memberService.create(role);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改会员")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody UmsMember role) {
        int count = memberService.update(id, role);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除会员")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = memberService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改会员状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Long id, @RequestBody UmsMember role) {
        int count = memberService.update(id, role);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    @ApiOperation("会员积分历史变化")
    @RequestMapping(value = "/historyIntegration/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsIntegrationChangeHistory>> list(
            @PathVariable Long id,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsIntegrationChangeHistory> integrationChangeHistory = memberService.integrationChangeHistoryList(id, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(integrationChangeHistory));
    }
}
