package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.commom.Result;
import com.example.demo.entity.HouseWithUser;
import com.example.demo.mapper.HouseWithUserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/housewithuser")
public class HouseWithUserController {
    @Resource
    HouseWithUserMapper BookWithUserMapper;

    @PostMapping("/insertNew")
    public Result<?> insertNew(@RequestBody HouseWithUser BookWithUser){
        BookWithUserMapper.insert(BookWithUser);
        return Result.success();
    }
    @PostMapping
    public Result<?> update(@RequestBody HouseWithUser BookWithUser){
        UpdateWrapper<HouseWithUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("number",BookWithUser.getNumber()).eq("id",BookWithUser.getId());
        BookWithUserMapper.update(BookWithUser, updateWrapper);
        return Result.success();
    }
//删除一条记录
    @PostMapping("/deleteRecord")
    public  Result<?> deleteRecord(@RequestBody HouseWithUser BookWithUser){
        Map<String,Object> map = new HashMap<>();
        map.put("number",BookWithUser.getNumber());
        map.put("id",BookWithUser.getId());
        BookWithUserMapper.deleteByMap(map);
        return Result.success();
    }

    @PostMapping("/deleteRecords")
    public Result<?> deleteRecords(@RequestBody List<HouseWithUser> BookWithUsers){
        int len = BookWithUsers.size();
        for(int i=0;i<len;i++) {
            HouseWithUser curRecord = BookWithUsers.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("number",curRecord.getNumber());
            map.put("id",curRecord.getId());
            BookWithUserMapper.deleteByMap(map);
        }
        return Result.success();
    }
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2,
                              @RequestParam(defaultValue = "") String search3){
        LambdaQueryWrapper<HouseWithUser> wrappers = Wrappers.<HouseWithUser>lambdaQuery();
        if(StringUtils.isNotBlank(search1)){
            wrappers.like(HouseWithUser::getNumber,search1);
        }
        if(StringUtils.isNotBlank(search2)){
            wrappers.like(HouseWithUser::getHouseName,search2);
        }
        if(StringUtils.isNotBlank(search3)){
            wrappers.like(HouseWithUser::getId,search3);
        }
        Page<HouseWithUser> BookPage =BookWithUserMapper.selectPage(new Page<>(pageNum,pageSize), wrappers);
        return Result.success(BookPage);
    }
}
