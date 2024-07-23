package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    void testInsert() {
        User user = new User();
//        user.setId(5L);
        user.setUsername("Lucy2");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
//        userMapper.saveUser(user);
//        userMapper.insertUser(user);
        userMapper.insert(user);
    }

    @Test
    void testSelectById() {
//        User user = userMapper.queryUserById(3L);
        User user = userMapper.selectById(4L);
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        List<Long> idList = List.of(1L, 2L, 3L, 4L);
//        List<User> users = userMapper.queryUserByIds(idList);
        List<User> users = userMapper.selectBatchIds(idList);
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(1314);
//        userMapper.updateUser(user);
        userMapper.updateUserBalance(user);
    }

    @Test
    void testDeleteUser() {
//        userMapper.deleteUser(5L);
        userMapper.deleteById(5L);
    }

    @Test
    void testSelectAllUser() {
        userInfoMapper.selectList(null);
    }

    //查询出名字中带o的，存款大于等于1000元的人的id、username、info、balance字段
    @Test
    void test() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(User::getUsername, "o")
                .ge(User::getBalance, 1000)
                .select(User::getId, User::getUsername, User::getInfo, User::getBalance);
        List<User> userList = userMapper.selectList(wrapper);
        userList.forEach(System.out::println);
    }

    //LambdaQueryWrapper 更新用户名为jack的用户的余额为2000
    @Test
    void test1() {
        User user = new User();
        user.setBalance(233);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "jack");
        int update = userMapper.update(user, wrapper);
        System.out.println("update = " + update);
    }

    //LambdaUpdateWrapper 更新用户名为jack的用户的余额为2000
    @Test
    void test2() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUsername, "jack")
                .set(User::getBalance, 2000);
        int update = userMapper.update(wrapper);
        System.out.println("update = " + update);
    }

    //更新id为1,2,4的用户的余额，扣200
    @Test
    void test3() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .in(User::getId, List.of(1L, 2L, 4L))
                .setSql("balance = balance - 200");
        int update = userMapper.update(wrapper);
        System.out.println("update = " + update);
    }

    //更新id为1,2,4的用户的余额，扣200 自定义SQL
    @Test
    void test4() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .in(User::getId, List.of(1L, 2L, 4L));
        int balance = 200;
        int update = userMapper.updateCostumSqlSegment(wrapper, balance);
        System.out.println("update = " + update);
    }
}