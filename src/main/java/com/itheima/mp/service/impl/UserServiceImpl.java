package com.itheima.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deductMoneyById(Long id, Integer money) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("该用户信息不存在！");
        }
        if (user.getBalance() == null) {
            throw new RuntimeException("用户余额为空！");
        }
        if (user.getBalance() < money) {
            throw new RuntimeException("余额不足！");
        }
        int balance = user.getBalance() - money;
        //余额小于等于0 设为冻结状态
        //方式一
//        user.setBalance(balance);
//        if (balance < 0) {
//            user.setStatus(2);
//        }
//        return updateById(user);
        //方式二 使用lambdaUpdate
        return lambdaUpdate().set(User::getBalance, balance)
                .set(balance == 0, User::getStatus, 2)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) //乐观锁  余额等于查到那一刻的数据
                .update();
    }

    @Override
    public Boolean deductMoneyById1(Long id, Integer money) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("该用户信息不存在！");
        }
        if (user.getBalance() == null) {
            throw new RuntimeException("用户余额为空！");
        }
        if (user.getBalance() < money) {
            throw new RuntimeException("余额不足！");
        }
        return baseMapper.deductMoneyById1(id, money);
    }
}
