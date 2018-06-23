package com.tipray.service;

import com.tipray.bean.*;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.track.LastTrack;
import com.tipray.bean.track.ReTrack;
import com.tipray.bean.upgrade.TerminalUpgradeInfo;
import com.tipray.bean.upgrade.UpgradeCancelVehicle;
import com.tipray.bean.upgrade.VehicleTree;
import com.tipray.core.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * VehicleService
 *
 * @author chenlong
 * @version 1.0 2017-12-22
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
     * @param id        车辆ID
     * @param carNumber 车牌号
     * @throws ServiceException
     */
    void deleteCar(Long id, String carNumber) throws ServiceException;

    /**
     * 根据Id获取车辆信息
     *
     * @param id 车辆ID
     * @return 车辆信息
     */
    Map<String, Object> getCarById(Long id);

    /**
     * 根据车牌号获取车辆信息
     *
     * @param carNo 车牌号
     * @return 车辆信息
     */
    Vehicle getByCarNo(String carNo);

    /**
     * 根据车牌号获取车辆ID
     *
     * @param carNo 车牌号
     * @return 车辆ID
     */
    Long getIdByCarNo(String carNo);

    /**
     * 根据车载终端ID获取车辆信息
     *
     * @param terminalId 车载终端ID
     * @return 车辆信息
     */
    Vehicle getCarByTerminalId(Integer terminalId);

    /**
     * 根据所属运输公司获取车辆信息
     *
     * @param transCompany 运输公司
     * @return 车辆信息
     */
    List<Vehicle> findByTransCompany(TransCompany transCompany);

    /**
     * 查询所有的车辆信息列表
     *
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
     * 为车辆选择控件提供车辆信息
     *
     * @param scope 选取范围（0 全部车辆，1 绑定车台的，2 在线）
     * @param comId 运输公司ID
     */
    Map<String, Object> selectCars(int scope, long comId);

    /**
     * 查询未使用的车台
     *
     * @return
     */
    List<Device> findUnusedTerminal();

    /**
     * 车辆绑定车载终端
     *
     * @param carNumber  车牌号
     * @param terminalId 车载终端设备ID
     * @throws ServiceException
     */
    void terminalBind(String carNumber, Integer terminalId) throws ServiceException;

    /**
     * 车辆解绑车载终端
     *
     * @param carNumber  车牌号
     * @param terminalId 车载终端设备ID
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
    List<ReTrack> findTracks(ReTrack carTrack);

    /**
     * 根据车辆ID获取车载终端ID
     *
     * @param id 车辆ID
     * @return {@link Integer} 车载终端ID
     */
    Integer getTerminalIdById(long id);

    /**
     * 根据车辆ID获取车牌号
     *
     * @param id 车辆ID
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
     * @param terminalConfig {@link VehicleTerminalConfig}
     * @throws ServiceException
     */
    void terminalConfig(VehicleTerminalConfig terminalConfig) throws ServiceException;

    /**
     * 获取车台已启用的功能
     *
     * @return 车台已启用的功能
     */
    Integer getFuncEnable();

    /**
     * 车台功能启用
     *
     * @param functionEnable 启用功能
     * @throws ServiceException
     */
    void terminalEnable(Integer functionEnable) throws ServiceException;

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
     * @param locks {@link List} {@link Lock}
     * @throws ServiceException
     */
    void bindLocks(List<Lock> locks) throws ServiceException;

    /**
     * 根据车牌号获取待绑定锁设备ID列表
     *
     * @param carNumber 车牌号
     * @return
     */
    List<Integer> findBindingLockDeviceIds(String carNumber);

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
     * @param remoteControlStatus 操作状态（0 未完成，1 远程操作请求中，2 远程操作完成）
     * @param remoteControlId     {@link Integer} 远程操作ID
     * @throws ServiceException
     */
    void updateRemoteControlStatus(Integer remoteControlStatus, Integer remoteControlId) throws ServiceException;

    /**
     * 根据车牌号获取车、司机相关信息
     *
     * @param carNumber 车牌号
     * @return
     */
    Map<String, Object> getCarAndDriverByCarNo(String carNumber);

    /**
     * 根据车牌号获取锁及其状态信息
     *
     * @param carNumber 车牌号
     * @return
     */
    List<Map<String, Object>> findlocksByCarNo(String carNumber);

    /**
     * 获取所有在线车辆信息（车牌号、仓数、设备ID、配送卡）
     *
     * @return {@link Map} {id, vehicle_number, vehicle_device_id, store_num, transport_card_id}
     */
    List<Map<String, Object>> findOnlineCarsForApp();

    /**
     * 获取所有车辆信息（车牌号、仓数、设备ID、配送卡）
     *
     * @return {@link Map} {id, vehicle_number, vehicle_device_id, store_num, transport_card_id}
     */
    List<Map<String, Object>> findAllCarsForApp();

    /**
     * 根据配送ID获取配送信息
     *
     * @param transportId      原配送ID
     * @param changedStationId 换站后加油站ID
     * @return
     */
    ChangeInfo getDistributionByTransportId(Long transportId, Long changedStationId);

    /**
     * 添加开锁重置记录
     *
     * @param map {@link Map} 参数集合
     * @return 成功插入记录条数，map中会添加一组键值对(key="id",value=记录ID)
     * @throws ServiceException
     */
    Long addLockResetRecord(Map<String, Object> map) throws ServiceException;

    /**
     * 批量更新开锁重置记录
     *
     * @param resetIds   {@link String} 开锁重置记录ID，英文逗号“,”分隔
     * @param resetState {@link Integer} 开锁重置状态（0：未完成 | 1：远程操作请求中 | 2：远程操作完成 | 3：车台主动重置完成）
     * @throws ServiceException
     */
    void batchUpdateResetRecord(String resetIds, Integer resetState) throws ServiceException;

    /**
     * 根据车牌号获取轨迹信息
     *
     * @param carNumbers 车牌号，英文逗号“,”分隔
     * @param beginTime  轨迹开始时间
     * @return
     */
    List<Map<String, Object>> findTracksByCarNumbers(String carNumbers, String beginTime);

    /**
     * 根据车牌号获取轨迹信息
     *
     * @param carNumber   车牌号
     * @param beginMillis 轨迹开始时间毫秒值
     * @return
     */
    List<Map<String, Object>> findTracksByCarNumber(String carNumber, Long beginMillis);

    /**
     * 获取已绑定车台的车辆的树形结构数据
     *
     * @return
     */
    List<VehicleTree> findBindedVehicleTree();

    /**
     * 根据车辆ID获取车辆实时状态
     *
     * @param vehicleId {@link Long} 车辆ID
     * @return {@link VehicleRealtimeStatus} 车辆实时状态
     */
    VehicleRealtimeStatus getVehicleRealtimeStatus(Long vehicleId);

    /**
     * 根据车载终端设备ID批量获取车牌号
     *
     * @param terminalIds {@link String} 车载终端设备ID，逗号分隔
     * @return {@link String} 车牌号集合
     */
    List<String> findCarNumbersByTerminalIds(String terminalIds);

    /**
     * 车辆在线状态监测
     *
     * @return {@link Long} 在线车辆集合（车辆ID，车牌号）
     */
    Map<Long, String> monitorVehicleOnline();

    /**
     * 查询最新在线车辆轨迹
     *
     * @return {@link LastTrack} 最新轨迹信息
     */
    List<LastTrack> findLastTracks();

    /**
     * 车台升级
     * @param terminalUpgradeInfo 车台升级信息
     * @param terminalIdList 车台ID集合
     */
    void terminalUpgrade(TerminalUpgradeInfo terminalUpgradeInfo, List<Integer> terminalIdList);

    /**
     * 查询未完成升级的车辆信息
     *
     * @return 未完成升级的车辆信息
     */
    List<UpgradeCancelVehicle> findUnfinishUpgradeVehicles();
}
