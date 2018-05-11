package com.tipray.util;

import com.tipray.bean.log.InfoManageLog;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.VehicleManageLogService;
import org.slf4j.Logger;

/**
 * 操作日志工具类
 *
 * @author chenlong
 * @version 1.0 2018-05-10
 */
public class OperateLogUtil {

    /**
     * 添加信息管理日志
     *
     * @param infoManageLog        {@link InfoManageLog} 信息管理日志对象
     * @param type                 {@link Integer} 日志类型
     * @param description          {@link String} 操作描述
     * @param infoManageLogService {@link InfoManageLogService} 信息管理日志业务对象
     * @param logger               {@link Logger} org.slf4j.Logger 对象
     * @return {@link Long} 信息管理日志记录ID
     */
    public static Long addInfoManageLog(InfoManageLog infoManageLog,
                                        Integer type,
                                        String description,
                                        InfoManageLogService infoManageLogService,
                                        Logger logger) {
        infoManageLog.setType(type);
        infoManageLog.setDescription(description);
        try {
            infoManageLogService.addInfoManageLog(infoManageLog);
            return infoManageLog.getId();
        } catch (Exception e) {
            logger.error("添加信息管理日志异常：infoManageLog={}, e={}", infoManageLog, e.toString());
            logger.debug("添加信息管理日志异常堆栈信息：", e);
            return null;
        }
    }

    /**
     * 添加车辆管理日志
     *
     * @param vehicleManageLog        {@link VehicleManageLog} 车辆管理日志对象
     * @param type                    {@link Integer} 日志类型
     * @param description             {@link String} 操作描述
     * @param token                   {@link String} 请求令牌UUID
     * @param vehicleManageLogService {@link VehicleManageLogService} 车辆管理日志业务对象
     * @param logger                  {@link Logger} org.slf4j.Logger 对象
     * @return {@link Long} 车辆管理日志记录ID
     */
    public static Long addVehicleManageLog(VehicleManageLog vehicleManageLog,
                                           Integer type,
                                           String description,
                                           String token,
                                           VehicleManageLogService vehicleManageLogService,
                                           Logger logger) {

        return addVehicleManageLog(vehicleManageLog, type, description, "请求发起成功！", token, vehicleManageLogService, logger);
    }

    /**
     * 添加车辆管理日志
     *
     * @param vehicleManageLog        {@link VehicleManageLog} 车辆管理日志对象
     * @param type                    {@link Integer} 日志类型
     * @param description             {@link String} 操作描述
     * @param result                  {@link String} 操作结果
     * @param token                   {@link String} 请求令牌UUID
     * @param vehicleManageLogService {@link VehicleManageLogService} 车辆管理日志业务对象
     * @param logger                  {@link Logger} org.slf4j.Logger 对象
     * @return {@link Long} 车辆管理日志记录ID
     */
    public static Long addVehicleManageLog(VehicleManageLog vehicleManageLog,
                                           Integer type,
                                           String description,
                                           String result,
                                           String token,
                                           VehicleManageLogService vehicleManageLogService,
                                           Logger logger) {
        vehicleManageLog.setType(type);
        vehicleManageLog.setDescription(description);
        vehicleManageLog.setResult(result);
        vehicleManageLog.setToken(token);
        try {
            vehicleManageLogService.addVehicleManageLog(vehicleManageLog);
            return vehicleManageLog.getId();
        } catch (Exception e) {
            logger.error("添加车辆管理日志异常：VehicleManageLog={}, e={}", vehicleManageLog, e.toString());
            logger.debug("添加车辆管理日志异常堆栈信息：", e);
            return null;
        }
    }

    /**
     * 更新车辆管理日志
     *
     * @param vehicleManageLog        {@link VehicleManageLog} 车辆管理日志对象
     * @param vehicleManageLogService {@link VehicleManageLogService} 车辆管理日志业务对象
     * @param logger                  {@link Logger} org.slf4j.Logger 对象
     */
    public static void updateVehicleManageLog(VehicleManageLog vehicleManageLog,
                                              VehicleManageLogService vehicleManageLogService,
                                              Logger logger) {
        try {
            vehicleManageLogService.updateVehicleManageLog(vehicleManageLog);
        } catch (Exception e) {
            logger.error("更新车辆管理日志异常：VehicleManageLog={}, e={}", vehicleManageLog, e.toString());
            logger.debug("添加车辆管理日志异常堆栈信息：", e);
        }
    }
}
