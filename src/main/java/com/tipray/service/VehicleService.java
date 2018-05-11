package com.tipray.service;

import java.util.List;
import java.util.Map;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.DropdownData;
import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.VehicleStatus;
import com.tipray.bean.VehicleTerminalConfig;
import com.tipray.bean.VehicleTrack;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.core.exception.ServiceException;

/**
 * VehicleService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface VehicleService {
	/**
	 * 新增车辆
	 * 
	 * @param car
	 * @throws ServiceException
	 */
    Vehicle addCar(Vehicle car, String driverIds) throws ServiceException;

	/**
	 * 修改车辆信息
	 * 
	 * @param Vehicle
	 * @throws ServiceException
	 */
    Vehicle updateCar(Vehicle car, String driverIds) throws ServiceException;

	/**
	 * 删除车辆
	 * 
	 * @param id
	 *            车辆ID
	 * @param carNumber
	 *            车牌号
	 * @throws ServiceException
	 */
    void deleteCar(Long id, String carNumber) throws ServiceException;

	/**
	 * 根据Id获取车辆信息
	 * 
	 * @param id
	 * @return
	 */
    Map<String, Object> getCarById(Long id);

	/**
	 * 根据车牌号获取车辆信息
	 * 
	 * @param carNo
	 * @return
	 */
    Vehicle getByCarNo(String carNo);

	/**
	 * 根据车载终端ID获取车辆信息
	 * 
	 * @param terminalId
	 * @return
	 */
    Vehicle getCarByTerminalId(Integer terminalId);

	/**
	 * 根据所属运输公司获取车辆信息
	 * 
	 * @param transCompany
	 * @return
	 */
    List<Vehicle> findByTransCompany(TransCompany transCompany);

	/**
	 * 查询所有的车辆信息列表
	 * 
	 * @param
	 * @return
	 */
    List<Vehicle> findAllCars();

	/**
	 * 获取车辆数目
	 * 
	 * @return
	 */
    long countCar(Vehicle car);

	/**
	 * 分页查询车辆信息
	 * 
	 * @param car
	 * @param page
	 * @return
	 */
    List<Vehicle> findByPage(Vehicle car, Page page);

	/**
	 * 分页查询车辆信息
	 * 
	 * @param car
	 * @param page
	 * @return
	 */
    GridPage<Vehicle> findCarsForPage(Vehicle car, Page page);

	/**
	 * 为车辆选择控件提供车辆信息
	 * 
	 * @return
	 */
    List<DropdownData> selectCars();

	/**
	 * 查询未使用的车台
	 * 
	 * @return
	 */
    List<Device> findUnusedTerminal();

	/**
	 * 车辆绑定车载终端
	 * 
	 * @param carNumber
	 *            车牌号
	 * @param terminalId
	 *            车载终端设备ID
	 * @throws ServiceException
	 */
    void terminalBind(String carNumber, Integer terminalId) throws ServiceException;

	/**
	 * 车辆解绑车载终端
	 * 
	 * @param carNumber
	 *            车牌号
	 * @param terminalId
	 *            车载终端设备ID
	 * @throws ServiceException
	 */
    void terminalUnbind(String carNumber, Integer terminalId) throws ServiceException;

	/**
	 * 查询绑定了车载终端的车辆
	 * 
	 * @return
	 */
    List<Vehicle> findBindedCars();

	/**
	 * 查询未绑定车载终端的车辆
	 * 
	 * @return
	 */
    List<Vehicle> findUnbindedCars();

	/**
	 * 获取未使用的锁设备ID列表
	 * 
	 * @return
	 */
    List<Integer> findUnusedLocks();

	/**
	 * 根据车牌号获取车辆当前状态
	 * 
	 * @param carNumber
	 * @return
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
	 * 获取车辆轨迹
	 * 
	 * @param carTrack
	 * @return
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
	 * 根据车辆ID获取车牌号
	 * 
	 * @param id  车辆ID
	 * @return 车牌号
	 */
    String getCarNumberById(long id);

	/**
	 * 批量添加车辆（待定）
	 * 
	 * @param vehicles
	 */
    void addCars(List<Vehicle> vehicles);

	/**
	 * 车台配置
	 * 
	 * @param terminalConfig
	 *            {@link VehicleTerminalConfig}
	 * @throws ServiceException
	 */
    void terminalConfig(VehicleTerminalConfig terminalConfig) throws ServiceException;
	
	/**
	 * 车台功能启用
	 * @param deviceId 车台设备ID
	 * @param functionEnable 启用功能
	 * @return 配置表记录ID
	 * @throws ServiceException
	 */
    Integer terminalEnable(Integer deviceId, Integer functionEnable) throws ServiceException;

	/**
	 * 根据车辆ID获取绑定的锁列表
	 * 
	 * @param carId
	 * @return
	 */
    List<Lock> findLocksByCarId(Long carId);

	/**
	 * 锁绑定
	 * 
	 * @param locks
	 *            {@link List} {@link Lock}
	 * @throws ServiceException
	 */
    void bindLocks(List<Lock> locks) throws ServiceException;

	/**
	 * 根据车辆ID获取待绑定锁设备ID列表
	 * 
	 * @param carId
	 * @return
	 */
    List<Integer> findBindingLockDeviceIds(Long carId);
	
	/**
	 * 根据待绑定锁信息集合获取车辆id集合
	 * 
	 * @param locks
	 * @return
	 */
    List<Lock> findVehicleIdByLocks(List<Lock> locks);

	/**
	 * 添加远程操作记录
	 * 
	 * @param map
	 * @return 远程操作ID
	 * @throws ServiceException
	 */
    Integer addRemoteControlRecord(Map<String, Object> map) throws ServiceException;

	/**
	 * 更新远程操作状态
	 * 
	 * @param remoteControlStatus
	 *            操作状态（0 未完成，1 远程操作请求中，2 远程操作完成）
	 * @param remoteControlId
	 *            {@link Integer} 远程操作ID
	 * @throws ServiceException
	 */
    void updateRemoteControlStatus(Integer remoteControlStatus, Integer remoteControlId) throws ServiceException;

	/**
	 * 更新远程变更车辆状态结果
	 * 
	 * @param remoteControlStatus
	 *            操作状态（0 未完成，1 远程操作请求中，2 远程操作完成）
	 * @param carStatus
	 *            车辆状态（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 | 5：应急）
	 * @param remoteControlId
	 *            远程操作ID
	 * @param carId
	 *            车辆ID
	 */
    void updateRemoteAlterStatusResulte(Integer remoteControlStatus, Integer carStatus, Integer remoteControlId,
                                        Long carId) throws ServiceException;

	/**
	 * 根据车牌号获取车、锁相关信息
	 * 
	 * @param carNumber
	 *            车牌号
	 * @return
	 */
    Map<String, Object> getCarAndLockByCarNo(String carNumber);

	/**
	 * 根据配送ID获取配送信息
	 * 
	 * @param transportId
	 *            原配送ID
	 * @param changedStationId
	 *            换站后加油站ID
	 * @return
	 */
    ChangeInfo getDistributionByTransportId(Long transportId, Long changedStationId);

	/**
	 * 添加开锁重置记录
	 * 
	 * @param map
	 *            {@link Map} 参数集合
	 * @return 成功插入记录条数，map中会添加一组键值对(key="id",value=记录ID)
	 * @throws ServiceException
	 */
    Long addLockResetRecord(Map<String, Object> map) throws ServiceException;

	/**
	 * 
	 * 批量更新开锁重置记录
	 * 
	 * @param resetIds
	 *            {@link String} 开锁重置记录ID，英文逗号“,”分隔
	 * @param resetState
	 *            {@link Integer} 开锁重置状态（0：未完成 | 1：远程操作请求中 | 2：远程操作完成 | 3：车台主动重置完成）
	 * @throws ServiceException
	 */
    void batchUpdateResetRecord(String resetIds, Integer resetState) throws ServiceException;

	/**
	 * 根据车牌号获取轨迹信息
	 * 
	 * @param carNumbers
	 *            车牌号，英文逗号“,”分隔
	 * @param beginTime
	 *            轨迹开始时间
	 * @return
	 */
    List<Map<String, Object>> findTracksByCarNumbers(String carNumbers, String beginTime);

	/**
	 * 获取已绑定车台的车辆的树形结构数据
	 * 
	 * @return
	 */
    List<Map<String, Object>> findBindedVehicleTree();
}
