package com.itheima.mp.service;

import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
class IUserServiceTest {
    @Autowired
    private IUserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        List<User> list = userService.list();
        list.forEach(System.out::println);
    }

    /**
     * 批量新增方式一
     * 普通for循环逐条插入 save方法 发送10万次网络请求 耗时：467419ms
     */
    @Test
    public void testSaveByFor() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            User user = buildUser(i);
            userService.save(user);
        }
        long end = System.currentTimeMillis();
        log.info("插入十万数据耗时：" + (end - start) + "ms");
    }

    /**
     * 批量新增方式二
     * MP的批量新增插入 saveBatch方法 基于预编译的批处理
     * JDBC PrepareStatement  耗时：11492ms
     * 发送100次网络请求
     * 多条insert into 语句
     */
    @Test
    public void testSaveByBatch() {
        long start = System.currentTimeMillis();
        List<User> userList = new ArrayList<>(1000);
        for (int i = 0; i < 100000; i++) {
            User user = buildUser(i);
            userList.add(user);
            if ((i + 1) % 1000 == 0) {
                userService.saveBatch(userList);
                userList.clear();
            }
        }
        long end = System.currentTimeMillis();
        log.info("插入十万数据耗时：" + (end - start) + "ms");
    }

    /**
     * 批量新增方式三
     * 批量插入 saveBatch
     * 开启mysql rewriteBatchedStatements
     * 配置jdbc参数 耗时：4688ms 4447ms
     */
    @Test
    public void testSaveByBatch1() {
        long start = System.currentTimeMillis();
        List<User> userList = new ArrayList<>(1000);
        for (int i = 0; i < 100000; i++) {
            User user = buildUser(i);
            userList.add(user);
            if ((i + 1) % 1000 == 0) {
                userService.saveBatch(userList);
                userList.clear();
            }
        }
        long end = System.currentTimeMillis();
        log.info("插入十万数据耗时：" + (end - start) + "ms");
    }


    /**
     * 批量新增方式四
     * mybatis foreach 批量插入
     * 100次网络请求
     * 耗时：4478ms 4414ms
     */
    @Test
    public void testSaveByMybatis() {
        long start = System.currentTimeMillis();
        List<User> userList = new ArrayList<>(1000);
        for (int i = 0; i < 100000; i++) {
            User user = buildUser(i);
            userList.add(user);
            if ((i + 1) % 1000 == 0) {
                userMapper.saveByBatch(userList);
                userList.clear();
            }
        }
        long end = System.currentTimeMillis();
        log.info("插入十万数据耗时：" + (end - start) + "ms");
    }

    private User buildUser(int i) {
        User user = new User();
        user.setUsername("tipsy" + i);
        user.setPassword(String.valueOf(i));
        user.setPhone("17206066864");
        user.setInfo("{\"age\": 28,\"name\":\"tipsy\",\"gender\":\"male\"}");
        user.setStatus(1);
        user.setBalance(i);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }
}