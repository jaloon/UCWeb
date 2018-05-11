package com.tipray.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.DropdownData;
import com.tipray.bean.VehicleStatus;
import com.tipray.bean.VehicleTerminalConfig;
import com.tipray.bean.VehicleTrack;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * VehicleDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface VehicleDao extends BaseDao<Vehicle> {
	/**
	 * 根据车辆ID获取配送卡ID
	 * @param id 车辆ID
	 * @return 配送卡ID
	 */
    Long getTransportCardIdById(Long id);
	
	/**
	 * 为车辆选择控件提供车辆信息
	 * 
	 * @return
	 */
    List<DropdownData> selectCars();

	/**
	 * 根据车牌号获取车辆信息
	 * 
	 * @param carNo
	 * @return {@link Vehicle}
	 */
    Vehicle getByCarNo(String carNo);

	/**
	 * 根据车载终端ID获取车辆信息
	 * 
	 * @param terminalId
	 * @return {@link Vehicle}
	 */
    Vehicle getCarByTerminalId(Integer terminalId);

	/**
	 * 根据所属运输公司获取车辆信息
	 * 
	 * @param transCompany
	 * @return {@link Vehicle} 车辆列表
	 */
    List<Vehicle> findByTransCompany(TransCompany transCompany);

	/**
	 * 查询未使用的车台
	 * 
	 * @return {@link Device} 未使用的车载终端列表
	 */
    List<Device> findUnusedTerminal();

	/**
	 * 车辆绑定车载终端
	 * 
	 * @param carNumber
	 *            车牌号
	 * @param terminalId
	 *            车载终端设备ID
	 */
    void terminalBind(@Param("carNumber") String carNumber, @Param("terminalId") Integer terminalId);

	/**
	 * 车辆解绑车载终端
	 * 
	 * @param carNumber
	 *            车牌号
	 */
    void terminalUnbind(@Param("carNumber") String carNumber);

	/**
	 * 查询绑定了车载终端的车辆
	 * 
	 * @return {@link Vehicle} 车辆列表
	 */
    List<Vehicle> findBindedCars();

	/**
	 * 查询未绑定车载终端的车辆
	 * 
	 * @return {@link Vehicle} 车辆列表
	 */
    List<Vehicle> findUnbindedCars();

	/**
	 * 根据车牌号获取车辆当前状态
	 * 
	 * @param carNumber
	 * @return {@link VehicleStatus}
	 */
    VehicleStatus getCarStatus(String carNumber);

	/**
	 * 根据车牌号获取车辆当前配送中的配送信息
	 * 
	 * @param carNumber
	 * @return
	 */
    List<ChangeInfo> getDistributionsByCarNumber(String carNumber);

	/**
	 * 根据配送ID获取配送信息
	 * 
	 * @param transportId
	 *            原配送ID
	 * @param changedStationId
	 *            换站后加油站ID
	 * @return
	 */
    ChangeInfo getDistributionByTransportId(@Param("transportId") Long transportId,
                                            @Param("changedStationId") Long changedStationId);

	/**
	 * 获取车辆轨迹
	 * 
	 * @param carTrack
	 * @return {@link VehicleTrack} 轨迹列表
	 */
    List<VehicleTrack> findTracks(VehicleTrack carTrack);

	/**
	 * 根据车辆ID获取车载终端ID
	 * 
	 * @param id
	 *            车辆ID
	 * @return {@link Integer} 车载终端ID
	 */
    Integer getTerminalIdById(long id);

	/**
	 * 
	 * @param id
	 *            车辆ID
	 * @return 车牌号
	 */
    String getCarNumberById(long id);

	/**
	 * 车台配置
	 * 
	 * @param terminalConfig
	 *            {@link VehicleTerminalConfig}
	 */
    void terminalConfig(VehicleTerminalConfig terminalConfig);

	/**
	 * 车台功能启用
	 * 
	 * @param map
	 *            参数<br>
	 *            deviceId 车台设备ID <br>
	 *            functionEnable 启用功能
	 */
    void terminalEnable(Map<String, Integer> map);

	/**
	 * 添加远程操作记录
	 * 
	 * @param map
	 */
    void addRemoteControlRecord(Map<String, Object> map);

	/**
	 * 根据车辆ID和远程操作类型获取远程操作ID
	 * 
	 * @param carId
	 *            {@link Long} 车辆ID
	 * @param type
	 *            {@link Integer} 远程操作类型（1：进油库 | 2：出油库 | 3：进加油站 | 4：出加油站 | 5：远程状态变更）
	 * @return {@link Integer} 远程操作ID
	 */
    Integer getRemoteControlIdByCarIdAndControlType(@Param("carId") Long carId, @Param("type") Integer type);

	/**
	 * 远程变更车辆状态
	 * 
	 * @param status
	 *            {@link Integer} 车辆状态（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 | 5：应急）
	 * @param remoteControlId
	 *            {@link Integer} 远程操作ID
	 * @param carId
	 *            {@link Long} 车辆ID
	 */
    void remoteAlterCarStatus(@Param("status") Integer status, @Param("remoteControlId") Integer remoteControlId,
                              @Param("carId") Long carId);

	/**
	 * 更新远程操作状态
	 * 
	 * @param remoteControlStatus
	 *            {@link Integer} 操作状态（0 未完成，1 远程操作请求中，2 远程操作完成）
	 * @param remoteControlId
	 *            {@link Integer} 远程操作ID
	 */
    void updateRemoteControlStatus(@Param("remoteControlStatus") Integer remoteControlStatus,
                                   @Param("remoteControlId") Integer remoteControlId);

	/**
	 * 添加开锁重置记录
	 * 
	 * @param map
	 *            {@link Map} 参数集合
	 * @return 成功插入记录条数，map中会添加一组键值对(key="id",value=记录ID)
	 */
    Long addLockResetRecord(Map<String, Object> map);

	/**
	 * 批量更新开锁重置记录
	 * 
	 * @param resetIds
	 *            {@link String} 开锁重置记录ID，英文逗号“,”分隔
	 * @param resetState
	 *            {@link Integer} 开锁重置状态（0：未完成 | 1：远程操作请求中 | 2：远程操作完成 | 3：车台主动重置完成）
	 */
    void batchUpdateResetRecord(@Param("resetIds") String resetIds, @Param("resetState") Integer resetState);

	/**
	 * 根据车牌号获取轨迹信息
	 * 
	 * @param carNumbers
	 *            {@link String} 车牌号，英文逗号“,”分隔
	 * @param beginTime
	 *            {@link String} 轨迹开始时间，格式：2018-03-29 19:18:29
	 * @return
	 */
    List<Map<String, Object>> findTracksByCarNumbers(@Param("carNumbers") String carNumbers,
                                                     @Param("beginTime") String beginTime);

	/**
	 * 获取已绑定车台的车辆的树形结构数据
	 * 
	 * @return
	 */
    List<Map<String, Object>> findBindedVehicleTree();
}
