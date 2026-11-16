package com.example.wms.config;

import com.example.wms.domain.User;
import com.example.wms.domain.Warehouse;
import com.example.wms.repository.UserRepository;
import com.example.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 首次启动初始化演示账号与仓库数据。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            userRepository.save(buildUser("admin", "admin123", "超级管理员", "admin@example.com",
                    "13800000000", User.Role.ADMIN, 1));
            userRepository.save(buildUser("zhangsan", "123456", "张三", "zhangsan@example.com",
                    "13900000001", User.Role.USER, 1));
            userRepository.save(buildUser("lisi", "123456", "李四", "lisi@example.com",
                    "13900000002", User.Role.USER, 0));
            log.info("已初始化演示用户：admin/admin123、zhangsan/123456、lisi/123456（禁用）");
        }

        if (warehouseRepository.count() == 0) {
            warehouseRepository.saveAll(List.of(
                    buildWarehouse("WH-SH-001", "上海外高桥一号仓", "上海市浦东新区外高桥保税区富特北路 118 号",
                            "王强", "13700000001", 12000, 8600, 1, "保税仓，支持跨境业务"),
                    buildWarehouse("WH-SH-002", "上海松江分拨仓", "上海市松江区新桥镇申港路 66 号",
                            "赵敏", "13700000002", 8000, 5200, 1, "华东区域分拨中心"),
                    buildWarehouse("WH-JS-001", "苏州吴中中转仓", "江苏省苏州市吴中区木渎镇金枫路 200 号",
                            "陈磊", "13700000003", 5000, 1800, 0, "临时停用，设备检修中")
            ));
            log.info("已初始化 3 条仓库演示数据");
        }
    }

    private User buildUser(String username, String rawPassword, String nickname, String email,
                           String phone, String role, int status) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

    private Warehouse buildWarehouse(String code, String name, String address, String manager,
                                     String phone, int capacity, int usedCapacity, int status, String remark) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(code);
        warehouse.setName(name);
        warehouse.setAddress(address);
        warehouse.setManager(manager);
        warehouse.setPhone(phone);
        warehouse.setCapacity(capacity);
        warehouse.setUsedCapacity(usedCapacity);
        warehouse.setStatus(status);
        warehouse.setRemark(remark);
        return warehouse;
    }
}
