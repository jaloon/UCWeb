package com.tipray.dao;

import com.tipray.bean.*;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.track.ReTrack;
import com.tipray.bean.upgrade.VehicleTree;
import com.tipray.core.annotation.MapResultAnno;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * VehicleDao
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@MyBatisAnno
public interface VehicleDao extends BaseDao<Vehicle> {
    /**
     * 根据车辆ID获取配送卡ID
     *
     * @param id {@link Long} 车辆ID
     * @return {@link Long} 配送卡ID
     */
    Long getTransportCardIdById(Long id);

    /**
     * 为车辆选择控件提供车辆信息
     *
     * @return {@link DropdownData} 车辆信息
     */
    List<DropdownData> selectCars();

    /**
     * 查找车辆对应运输公司
     *
     * @param comId 运输公司ID
     * @return
     */
    List<Map<String, Object>> findComsWithCar(long comId);

    /**
     * 查找绑定车台车辆对应运输公司
     *
     * @param comId 运输公司ID
     * @return
     */
    List<Map<String, Object>> findComsWithBindedCar(long comId);

    /**
     * 查找在线车辆对应运输公司
     *
     * @param comId 运输公司ID
     * @return
     */
    List<Map<String, Object>> findComsWithOnlineCar(long comId);

    /**
     * 查找车辆车牌号
     *
     * @param comId 运输公司ID
     * @return
     */
    List<Map<String, Object>> findCarNumbers(long comId);

    /**
     * 查找绑定车台车辆车牌号
     *
     * @param comId 运输公司ID
     * @return
     */
    List<Map<String, Object>> findBindedCarNumbers(long comId);

    /**
     * 查找在线车辆车牌号
     *
     * @param comId 运输公司ID
     * @return
     */
    List<Map<String, Object>> findOnlineCarNumbers(long comId);

    /**
     * 根据车牌号获取车辆信息
     *
     * @param carNo {@link String} 车牌号
     * @return {@link Vehicle} 车辆信息
     */
    Vehicle getByCarNo(String carNo);

    /**
     * 根据车牌号获取车辆ID
     *
     * @param carNo {@link String} 车牌号
     * @return {@link Long} 车辆ID
     */
    Long getIdByCarNo(String carNo);

    /**
     * 根据车牌号获取仓数
     * @param carNo 车牌号
     * @return 仓数
     */
    Integer getStoreNumByCarNo(String carNo);

    /**
     * 根据车载终端ID获取车辆信息
     *
     * @param terminalId {@link Integer} 车载终端ID
     * @return {@link Vehicle} 车辆信息
     */
    Vehicle getCarByTerminalId(Integer terminalId);

    /**
     * 根据所属运输公司获取车辆信息
     *
     * @param transCompany {@link TransCompany} 运输公司
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
     * @param carNumber  {@link String} 车牌号
     * @param terminalId {@link Long} 车载终端设备ID
     */
    void terminalBind(@Param("carNumber") String carNumber, @Param("terminalId") Integer terminalId);

    /**
     * 车辆解绑车载终端
     *
     * @param carNumber {@link String} 车牌号
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
     * @param carNumber {@link String} 车牌号
     * @return {@link VehicleStatus} 车辆状态
     */
    VehicleStatus getCarStatus(String carNumber);

    /**
     * 根据车牌号获取车辆当前配送中的配送信息
     *
     * @param carNumber {@link String} 车牌号
     * @return {@link ChangeInfo} 配送信息
     */
    List<ChangeInfo> getDistributionsByCarNumber(String carNumber);

    /**
     * 根据配送ID获取配送信息
     *
     * @param transportId      {@link Long} 原配送ID
     * @param changedStationId {@link Long} 换站后加油站ID
     * @return {@link ChangeInfo} 配送信息
     */
    ChangeInfo getDistributionByTransportId(@Param("transportId") Long transportId,
                                            @Param("changedStationId") Long changedStationId);

    /**
     * 获取车辆轨迹
     *
     * @param carTrack {@link ReTrack} 轨迹查询统计
     * @return {@link ReTrack} 轨迹列表
     */
    List<ReTrack> findTracks(ReTrack carTrack);

    /**
     * 根据车辆ID获取车载终端ID
     *
     * @param id {@link Long} 车辆ID
     * @return {@link Integer} 车载终端ID
     */
    Integer getTerminalIdById(Long id);

    /**
     * @param id {@link Long} 车辆ID
     * @return {@link String}  车牌号
     */
    String getCarNumberById(Long id);

    /**
     * 车台配置
     *
     * @param terminalConfig {@link VehicleTerminalConfig} 车台配置信息
     */
    void terminalConfig(VehicleTerminalConfig terminalConfig);

    /**
     * 查询台功能启用配置记录数目
     */
    Integer countTerminalEnable();

    /**
     * 查询台功能启用配置记录数目
     */
    Integer getTerminalEnable();

    /**
     * 更新车台功能启用配置
     * @param functionEnable {@link Integer} 启用功能
     */
    void updateTerminalEnable(Integer functionEnable);

    /**
     * 添加车台功能启用配置
     * @param functionEnable {@link Integer} 启用功能
     */
    void addTerminalEnable(Integer functionEnable);

    /**
     * 添加远程操作记录
     *
     * @param map 远程操作信息
     */
    void addRemoteControlRecord(Map<String, Object> map);

    /**
     * 添加远程操作记录
     *
     * @param map 远程状态变更信息
     */
    void addRemoteStatusAlterRecord(Map<String, Object> map);

    /**
     * 根据车辆ID和远程操作类型获取远程操作ID
     *
     * @param carId {@link Long} 车辆ID
     * @param type  {@link Integer} 远程操作类型（1：进油库 | 2：出油库 | 3：进加油站 | 4：出加油站 | 5：远程状态变更）
     * @return {@link Integer} 远程操作ID
     */
    Integer getRemoteControlIdByCarIdAndControlType(@Param("carId") Long carId, @Param("type") Integer type);

    /**
     * 更新远程操作状态
     *
     * @param remoteControlStatus {@link Integer} 操作状态（0 未完成，1 远程操作请求中，2 远程操作完成）
     * @param remoteControlId     {@link Integer} 远程操作ID
     */
    void updateRemoteControlStatus(@Param("remoteControlStatus") Integer remoteControlStatus,
                                   @Param("remoteControlId") Integer remoteControlId);

    /**
     * 添加开锁重置记录
     *
     * @param map {@link Map} 参数集合
     * @return 成功插入记录条数，map中会添加一组键值对(key="id",value=记录ID)
     */
    Long addLockResetRecord(Map<String, Object> map);

    /**
     * 批量更新开锁重置记录
     *
     * @param resetIds   {@link String} 开锁重置记录ID，英文逗号“,”分隔
     * @param resetState {@link Integer} 开锁重置状态（0：未完成 | 1：远程操作请求中 | 2：远程操作完成 | 3：车台主动重置完成）
     */
    void batchUpdateResetRecord(@Param("resetIds") String resetIds, @Param("resetState") Integer resetState);

    /**
     * 根据车牌号获取轨迹信息
     *
     * @param carNumbers {@link String} 车牌号，英文逗号“,”分隔
     * @param beginTime  {@link String} 轨迹开始时间，格式：2018-03-29 19:18:29
     * @return 轨迹信息
     */
    List<Map<String, Object>> findTracksByCarNumbers(@Param("carNumbers") String carNumbers,
                                                     @Param("beginTime") String beginTime);

    /**
     * 获取已绑定车台的车辆的树形结构数据
     *
     * @return 车辆的树形结构数据
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
     * 根据车牌号获取车辆信息（车牌号、仓数、设备ID、配送卡）
     *
     * @param carNo {@link String} 车牌号
     * @return {@link Map} {id, vehicle_number, vehicle_device_id, store_num, transport_card_id}
     */
    Map<String, Object> getByCarNoForApp(String carNo);

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
     * 获取在线车辆ID集合
     *
     * @return {@link Map} 在线车辆集合（carId：车辆ID，carNumber：车牌号）
     */
    @MapResultAnno(keyType = Long.class, valType = String.class)
    Map<Long, String> findOnlineCarIds();

    /**
     * 更新最后在线时间距现在超过6分钟的车辆在线状态为离线（超时用400秒）
     */
    void updateTimeoutOfflineCars();

    /**
     * 根据车牌号获取车辆id
     *
     * @param carNumbers {@link String} 车牌号，英文逗号“,”分隔
     * @return {@link Map} 车辆id集合(vehicle_id 车辆ID， vehicle_number 车牌号)
     */
    @MapResultAnno(keyType = Long.class, valType = String.class)
    Map<Long, String> findCarIdsByCarNumbers(String carNumbers);

    /**
     * 根据车辆ID查询车辆是否在线
     * @param carId {@link Long} 车辆ID
     * @return {@link Integer} 1 在线，0 离线
     */
    Integer getOnline(Long carId);
}
