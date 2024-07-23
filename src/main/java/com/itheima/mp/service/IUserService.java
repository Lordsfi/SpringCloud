package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.po.User;

public interface IUserService extends IService<User> {
    Boolean deductMoneyById(Long id, Integer money);

    Boolean deductMoneyById1(Long id, Integer money);
}
