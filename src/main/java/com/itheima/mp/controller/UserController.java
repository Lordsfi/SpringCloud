package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Api(tags = "用户管理接口")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    //方式一和二 字段注入
//    @Autowired
//    @Resource
//    private IUserService userService;

    //方式三 构造器注入
//    private IUserService userService;
//
//    public UserController(IUserService userService) {
//        this.userService = userService;
//    }

    //方式四 构造器配合lombok注解注入
    private final IUserService userService;

    @ApiOperation("新增用户接口")
    @PostMapping
    public Boolean saveUser(@RequestBody UserFormDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);
        return userService.save(user);
    }

    @ApiOperation("根据id删除用户接口")
    @DeleteMapping("/{id}")
    public Boolean deleteUser(@ApiParam("用户id") @PathVariable("id") Long id) {
        return userService.removeById(id);
    }

    @ApiOperation("修改用户接口")
    @PutMapping
    public Boolean updateUser(@RequestBody UserFormDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);
        return userService.updateById(user);
    }

    @ApiOperation("根据id查找用户接口")
    @GetMapping("/{id}")
    public UserVO getUserById(@ApiParam("用户id") @PathVariable("id") Long id) {
        User user = userService.getById(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        List<Address> addressList = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, id)
                .list();
        userVO.setAddressVOList(BeanUtil.copyToList(addressList, AddressVO.class));
        return userVO;
    }

    @ApiOperation("根据id批量查找用户接口")
    @GetMapping
    public List<UserVO> getUserByIds(@ApiParam("用户id集合") @RequestParam("idList") List<Long> idList) {
        List<User> userList = userService.listByIds(idList);
        List<UserVO> userVOList = BeanUtil.copyToList(userList, UserVO.class);
        List<Long> userIdList = userList.stream()
                .map(User::getId)
                .collect(Collectors.toList());
        List<Address> addressList = Db.lambdaQuery(Address.class)
                .in(Address::getUserId, userIdList)
                .list();
        Map<Long, List<Address>> groupMap = new HashMap<>(0);
        if (!addressList.isEmpty()) {
            groupMap = addressList.stream().collect(Collectors.groupingBy(Address::getUserId));
        }
        if (!groupMap.isEmpty()) {
            for (UserVO userVO : userVOList) {
                List<Address> addresses = groupMap.get(userVO.getId());
                userVO.setAddressVOList(BeanUtil.copyToList(addresses, AddressVO.class));
            }
        }
        return userVOList;
    }

    @ApiOperation("根据id扣减余额接口")
    @GetMapping("/{id}/deduction/{money}")
    public Boolean deductMoneyById(@ApiParam("用户id") @PathVariable("id") Long id, @ApiParam("扣除金额") @PathVariable("money") Integer money) {
        return userService.deductMoneyById(id, money);
    }

    @ApiOperation("根据id扣减余额接口1")
    @GetMapping("/{id}/deduction1/{money}")
    public Boolean deductMoneyById1(@ApiParam("用户id") @RequestParam("id") Long id, @ApiParam("扣除余额") @RequestParam("money") Integer money) {
        return userService.deductMoneyById1(id, money);
    }

    @ApiOperation("模糊查询用户接口")
    @GetMapping("/getByCondition")
    public List<UserVO> getByCondition(UserQuery userQuery) {
        List<User> userList = userQuery == null ? userService.list() : userService.lambdaQuery()
                .like(StringUtils.isNotBlank(userQuery.getName()), User::getUsername, userQuery.getName())
                .eq(userQuery.getStatus() != null, User::getStatus, userQuery.getStatus())
                .ge(userQuery.getMinBalance() != null, User::getBalance, userQuery.getMinBalance())
                .le(userQuery.getMaxBalance() != null, User::getBalance, userQuery.getMaxBalance())
                .list();
        return BeanUtil.copyToList(userList, UserVO.class);
    }
}
