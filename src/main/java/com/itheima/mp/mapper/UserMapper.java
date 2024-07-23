package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    void saveUser(User user);

    void deleteUser(Long id);

    void updateUser(User user);

    User queryUserById(@Param("id") Long id);

    List<User> queryUserByIds(@Param("ids") List<Long> ids);

    void insertUser(User user);

    void updateUserBalance(User user);

    //    int updateCostumSqlSegment(@Param("ew") LambdaQueryWrapper<User> wrapper, Integer balance);
    int updateCostumSqlSegment(@Param(Constants.WRAPPER) LambdaQueryWrapper<User> wrapper, Integer balance);

    @Update("update user set balance = balance - #{money} where id = #{id}")
    Boolean deductMoneyById1(@Param("id") Long id, @Param("money") Integer money);

    void saveByBatch(List<User> userList);
}
