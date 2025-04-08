package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.management.OperatingSystemMXBean;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Order;
import com.gk.study.entity.Thing;
import com.gk.study.entity.VisitData;
import com.gk.study.mapper.OrderMapper;
import com.gk.study.mapper.OverviewMapper;
import com.gk.study.mapper.ThingMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 系统概览控制器
 * 提供系统运行状态和业务数据的统计接口
 * 主要功能包括：
 * 1. 获取系统运行信息（CPU、内存、JVM等）
 * 2. 统计业务数据（商品、订单、访问量等）
 * 3. 获取热门商品和分类
 * 4. 统计网站访问数据
 * 
 * @author Administrator
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/overview")
public class OverViewController {

    // 注入商品数据访问对象
    @Autowired
    ThingMapper thingMapper;

    // 注入订单数据访问对象
    @Autowired
    OrderMapper orderMapper;

    // 注入概览数据访问对象
    @Autowired
    OverviewMapper overviewMapper;

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(OverViewController.class);

    /**
     * 获取系统运行信息
     * 包括：
     * 1. 系统基本信息（名称、版本、处理器等）
     * 2. 操作系统信息（架构、名称、CPU核心数）
     * 3. 系统资源使用情况（CPU负载、内存使用率）
     * 4. JVM版本信息
     * 
     * @return APIResponse 包含系统信息的响应对象
     */
    @RequestMapping(value = "/sysInfo", method = RequestMethod.GET)
    public APIResponse sysInfo() {
        Map<String, String> map = new HashMap<>();
        
        // 设置系统基本信息
        map.put("sysName", "后台系统");
        map.put("versionName", "1.0");
        map.put("processor", "2");
        map.put("sysLan", "En");
        map.put("sysZone", "东八区");

        // 获取操作系统信息
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        map.put("pf", osmxb.getArch());
        map.put("osName", osmxb.getName());
        map.put("cpuCount", String.valueOf(osmxb.getAvailableProcessors()));

        // 设置数字格式化
        DecimalFormat df = new DecimalFormat("#,##0.0");

        // 获取CPU使用率
        double cpuLoad = osmxb.getSystemCpuLoad();
        // 获取内存使用情况
        double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        double value = freePhysicalMemorySize / totalvirtualMemory;
        double percentMemoryLoad = ((1 - value) * 100);
        
        // 设置系统资源使用信息
        map.put("cpuLoad", df.format(cpuLoad * 100));
        map.put("memory", df.format(totalvirtualMemory / 1024 / 1024 / 1024));
        map.put("usedMemory", df.format((totalvirtualMemory - freePhysicalMemorySize) / 1024 / 1024 / 1024));
        map.put("percentMemory", df.format(percentMemoryLoad));

        // 设置JVM版本信息
        map.put("jvmVersion", System.getProperty("java.version"));

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", map);
    }

    /**
     * 获取业务数据统计信息
     * 包括：
     * 1. 商品统计（总数、七日新增）
     * 2. 订单统计（未付订单、已付订单、取消订单）
     * 3. 用户统计（各类订单的用户数）
     * 4. 热门数据（商品、分类）
     * 5. 网站访问数据（PV、UV）
     * 
     * @return APIResponse 包含统计数据的响应对象
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public APIResponse count() {
        Map<String, Object> map = new HashMap<>();

        // 统计商品总数
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        long spzs = thingMapper.selectCount(queryWrapper);
        map.put("spzs", spzs);

        // 计算7天前的时间戳
        long now = System.currentTimeMillis();
        long sevenMillis = now - 7 * 24 * 60 * 60 * 1000;

        // 统计7日内新增商品数
        queryWrapper.ge("create_time", sevenMillis);
        long qrxz = thingMapper.selectCount(queryWrapper);
        map.put("qrxz", qrxz);

        // 统计未支付订单数
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "1");
        long wfdd = orderMapper.selectCount(orderQueryWrapper);
        map.put("wfdd", wfdd);

        // 统计未支付订单用户数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "1");
        orderQueryWrapper.groupBy("user_id");
        map.put("wfddrs", orderMapper.selectList(orderQueryWrapper).size());

        // 统计已支付订单数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "2");
        long yfdd = orderMapper.selectCount(orderQueryWrapper);
        map.put("yfdd", yfdd);

        // 统计已支付订单用户数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "2");
        orderQueryWrapper.groupBy("user_id");
        map.put("yfddrs", orderMapper.selectList(orderQueryWrapper).size());

        // 统计取消订单数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "7");
        long qxdd = orderMapper.selectCount(orderQueryWrapper);
        map.put("qxdd", qxdd);

        // 统计取消订单用户数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "7");
        orderQueryWrapper.groupBy("user_id");
        map.put("qxddrs", orderMapper.selectList(orderQueryWrapper).size());

        // 获取热门商品数据
        List<Object> popularThings = overviewMapper.getPopularThing();
        map.put("popularThings", popularThings);

        // 获取热门分类数据
        List<Object> popularClassification = overviewMapper.getPopularClassification();
        map.put("popularClassification", popularClassification);

        // 统计网站访问数据
        List<Object> visitList = new ArrayList<>();
        List<String> sevenList = getSevenDate();
        for(String day: sevenList){
            Map<String, String> visitMap = new HashMap<>();
            visitMap.put("day", day);
            int pv = 0;
            int uv = 0;
            // 获取指定日期的访问数据
            List<VisitData> webVisitData = overviewMapper.getWebVisitData(day);
            for(VisitData visitData: webVisitData) {
                pv += visitData.count;
            }
            uv = webVisitData.size();
            visitMap.put("pv", String.valueOf(pv));
            visitMap.put("uv", String.valueOf(uv));
            visitList.add(visitMap);
        }
        map.put("visitList", visitList);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", map);
    }

    /**
     * 获取最近7天的日期列表
     * 格式：yyyy-MM-dd
     * 
     * @return List<String> 最近7天的日期列表
     */
    public static List<String> getSevenDate() {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 获取最近7天的日期
        for (int i = 0; i < 7; i++) {
            Date date = DateUtils.addDays(new Date(), -i);
            String formatDate = sdf.format(date);
            dateList.add(formatDate);
        }
        // 反转列表，使日期按时间顺序排列
        Collections.reverse(dateList);
        return dateList;
    }
}
