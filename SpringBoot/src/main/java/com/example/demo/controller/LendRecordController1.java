package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.demo.commom.Result;
import com.example.demo.entity.LendRecord;
import com.example.demo.mapper.LendRecordMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/LendRecord1")
public class LendRecordController1 {
    @Resource
    LendRecordMapper LendRecordMapper;
    @PutMapping
    public Result<?> update2(@RequestBody LendRecord lendRecord){
        UpdateWrapper<LendRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("number",lendRecord.getNumber()).eq("user_id",lendRecord.getUserId()).eq("borrownum",lendRecord.getBorrownum());
        LendRecord lendrecord = new LendRecord();
        lendrecord.setReturnTime(lendRecord.getReturnTime());
        lendrecord.setStatus(lendRecord.getStatus());
        LendRecordMapper.update(lendrecord, updateWrapper);
        return Result.success();
    }



}
