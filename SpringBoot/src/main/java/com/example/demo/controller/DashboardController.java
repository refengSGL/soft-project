package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.LoginUser;
import com.example.demo.commom.Result;
import com.example.demo.entity.House;
import com.example.demo.entity.LendRecord;
import com.example.demo.entity.User;
import com.example.demo.mapper.HouseMapper;
import com.example.demo.mapper.LendRecordMapper;
import com.example.demo.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/dashboard")
public class DashboardController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private LendRecordMapper lendRecordMapper;
    @Resource
    private HouseMapper bookMapper;

    @GetMapping
    public Result<?> dashboardrecords(){
        int visitCount = LoginUser.getVisitCount();
        QueryWrapper<User> queryWrapper1=new QueryWrapper();
        int userCount = userMapper.selectCount(queryWrapper1);
        QueryWrapper<LendRecord> queryWrapper2=new QueryWrapper();
        int lendRecordCount = lendRecordMapper.selectCount(queryWrapper2);
        QueryWrapper<House> queryWrapper3=new QueryWrapper();
        int bookCount = bookMapper.selectCount(queryWrapper3);
        Map<String, Object> map = new HashMap<>();//键值对形式
        map.put("visitCount", visitCount);//放置visitCount到map中
        map.put("userCount", userCount);

        map.put("lendRecordCount", lendRecordCount);

        // 看看是不是这一步出问题了 打个日志
        // 确实是这步的问题
        log.info("bookCount:{}",bookCount);
        map.put("houseCount", bookCount);
        return Result.success(map);
    }
}
