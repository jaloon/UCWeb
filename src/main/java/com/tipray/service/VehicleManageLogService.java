package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.log.VehicleManageLog;
import com.tipray.core.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * VehicleManageLogService
 *
 * @author chenlong
 * @version 1.0 2018-04-08
 */
public interface VehicleManageLogService {
    /**
     * 新增车辆管理日志
     *
     * @param VehicleManageLog
     * @throws ServiceException
     */
    VehicleManageLog addVehicleManageLog(VehicleManageLog vehicleManageLog) throws ServiceException;

    /**
     * 更新车辆管理日志
     *
     * @param VehicleManageLog
     * @throws ServiceException
     */
    VehicleManageLog updateVehicleManageLog(VehicleManageLog vehicleManageLog) throws ServiceException;

    /**
     * 根据Id获取车辆管理日志信息
     *
     * @param id
     * @return
     */
    VehicleManageLog getVehicleManageLogById(Long id);

    /**
     * 查询所有的车辆管理日志信息列表
     *
     * @param
     * @return
     */
    List<VehicleManageLog> findAllVehicleManageLogs();

    /**
     * 获取车辆管理日志数目
     *
     * @return
     */
    long countVehicleManageLog(VehicleManageLog vehicleManageLog);

    /**
     * 分页查询车辆管理日志信息
     *
     * @param VehicleManageLog
     * @param page
     * @return
     */
    List<VehicleManageLog> findByPage(VehicleManageLog vehicleManageLog, Page page);

    /**
     * 分页查询车辆管理日志信息
     *
     * @param VehicleManageLog
     * @param page
     * @return
     */
    GridPage<VehicleManageLog> findVehicleManageLogsForPage(VehicleManageLog vehicleManageLog, Page page);

    /**
     * 根据UDP业务ID和请求发起时间获取UDP应答结果
     *
     * @param map {@link Map} 参数 <br>
     *            userId {@link Long} 用户ID <br>
     *            isApp {@link Integer} 是否手机APP操作 <br>
     *            udpBizId {@link Short} UDP业务ID <br>
     *            token {@link String} UUID
     * @return {@link Map}<br>
     * token {@link String} UUID<br>
     * description {@link String} 请求描述<br>
     * result {@link String} 请求结果<br>
     * responseMsg {@link ResponseMsg} UDP应答信息<br>
     */
    Map<String, Object> findUdpResult(Map<String, Object> map);

    /**
     * 根据UDP业务ID和请求发起时间获取UDP应答结果
     *
     * @param map {@link Map} 参数 <br>
     *            userId {@link Long} 用户ID <br>
     *            isApp {@link Integer} 是否手机APP操作 <br>
     *            udpBizId {@link Short} UDP业务ID <br>
     *            token {@link String} UUID
     * @return {@link VehicleManageLog}
     */
    VehicleManageLog findUdpReplyLog(Map<String, Object> map);
}
