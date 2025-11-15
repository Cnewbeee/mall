package com.macro.mall.controller;

import com.github.pagehelper.Page;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.service.UmsMemberLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 会员等级管理Controller
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "UmsMemberLevelController")
@Tag(name = "UmsMemberLevelController", description = "会员等级管理")
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {
    @Autowired
    private UmsMemberLevelService memberLevelService;

    @ApiOperation("分页查询会员等级")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberLevel>> list(
            @RequestParam(value = "defaultStatus", required = false) Integer defaultStatus,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<UmsMemberLevel> page = memberLevelService.list(defaultStatus, name, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @ApiOperation("创建会员等级")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@Valid @RequestBody UmsMemberLevel memberLevel) {
        int count = memberLevelService.create(memberLevel);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("创建会员等级失败");
    }

    @ApiOperation("获取会员等级详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<UmsMemberLevel> getDetail(@PathVariable Long id) {
        UmsMemberLevel memberLevel = memberLevelService.getItem(id);
        return CommonResult.success(memberLevel);
    }

    @ApiOperation("更新会员等级")
    @PutMapping("/{id}")
    @ResponseBody
    public CommonResult update(
            @PathVariable Long id,
            @Valid @RequestBody UmsMemberLevel memberLevel) {

        int count = memberLevelService.update(id, memberLevel);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("更新会员等级失败");
    }

    @ApiOperation("批量删除会员等级")
    @DeleteMapping("/delete")
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = memberLevelService.delete(ids);
        return CommonResult.success(count);
    }
}
