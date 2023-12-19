package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.commom.Result;
import com.example.demo.entity.House;
import com.example.demo.mapper.HouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/house")
public class HouseController {
    @Autowired
    HouseMapper BookMapper;

    @PostMapping
    public Result save(@RequestBody House Book){
        BookMapper.insert(Book);
        return Result.success();
    }
    @PutMapping
    public  Result<?> update(@RequestBody House Book){
        BookMapper.updateById(Book);
        return Result.success();
    }

    //    批量删除
    @PostMapping("/deleteBatch")
    public  Result<?> deleteBatch(@RequestBody List<Integer> ids){
        BookMapper.deleteBatchIds(ids);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        BookMapper.deleteById(id);
        return Result.success();
    }
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2,
                              @RequestParam(defaultValue = "") String search3){
        LambdaQueryWrapper<House> wrappers = Wrappers.<House>lambdaQuery();
        if(StringUtils.isNotBlank(search1)){
            wrappers.like(House::getNumber,search1);
        }
        if(StringUtils.isNotBlank(search2)){
            wrappers.like(House::getName,search2);
        }
        if(StringUtils.isNotBlank(search3)){
            wrappers.like(House::getAuthor,search3);
        }
        Page<House> BookPage =BookMapper.selectPage(new Page<>(pageNum,pageSize), wrappers);
        return Result.success(BookPage);
    }
}
