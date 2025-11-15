package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.model.UmsMemberLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 测试会员控制器（仅用于开发测试）
 */
@Controller
@Api(tags = "TestMemberController")
@Tag(name = "TestMemberController", description = "测试会员管理")
@RequestMapping("/test/member")
public class TestMemberController {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiOperation("创建测试会员（无需验证码）")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createTestMember(@RequestParam String username,
                                         @RequestParam String password,
                                         @RequestParam String telephone) {
        try {
            // 检查是否已有该用户
            UmsMember existingMember = getMemberByUsername(username);
            if (existingMember != null) {
                return CommonResult.failed("该用户已经存在");
            }

            // 创建新会员
            UmsMember umsMember = new UmsMember();
            umsMember.setUsername(username);
            umsMember.setPhone(telephone);
            umsMember.setPassword(passwordEncoder.encode(password));
            umsMember.setCreateTime(new Date());
            umsMember.setStatus(1);
            umsMember.setNickname(username);
            umsMember.setIntegration(1000); // 默认积分

            // 获取默认会员等级并设置
            UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
            levelExample.createCriteria().andDefaultStatusEqualTo(1);
            List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
            if (!memberLevelList.isEmpty()) {
                umsMember.setMemberLevelId(memberLevelList.get(0).getId());
            }

            memberMapper.insert(umsMember);

            return CommonResult.success(null, "测试会员创建成功");
        } catch (Exception e) {
            return CommonResult.failed("创建测试会员失败: " + e.getMessage());
        }
    }

    @ApiOperation("重置会员密码")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult resetPassword(@RequestParam String username,
                                      @RequestParam String newPassword) {
        try {
            UmsMember member = getMemberByUsername(username);
            if (member == null) {
                return CommonResult.failed("用户不存在");
            }

            UmsMember updateMember = new UmsMember();
            updateMember.setId(member.getId());
            updateMember.setPassword(passwordEncoder.encode(newPassword));
            memberMapper.updateByPrimaryKeySelective(updateMember);

            return CommonResult.success(null, "密码重置成功");
        } catch (Exception e) {
            return CommonResult.failed("密码重置失败: " + e.getMessage());
        }
    }

    private UmsMember getMemberByUsername(String username) {
        UmsMember member = new UmsMember();
        member.setUsername(username);
        List<UmsMember> memberList = memberMapper.selectList(member);
        return memberList.isEmpty() ? null : memberList.get(0);
    }
}