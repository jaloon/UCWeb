package com.tipray.dao;

import com.tipray.bean.alarm.AlarmDevice;
import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * AlarmRecordDao
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@MyBatisAnno
public interface AlarmRecordDao extends BaseDao<AlarmRecord> {
    /**
     * 为消除报警获取报警记录
     *
     * @param id 报警ID
     * @return 报警记录
     */
    AlarmRecord getAlarmForEliById(Long id);

    /**
     * 根据报警ID集合获取报警记录
     *
     * @param ids       {@link String} 报警ID集合，英文“,”分隔
     * @param vehicleId {@link Long} 车辆ID
     * @return 报警记录
     */
    List<AlarmRecord> getAlarmRecordsByIdsAndCar(@Param("ids") String ids, @Param("vehicleId") Long vehicleId);

    /**
     * 根据报警ID集合获取报警设备个数
     *
     * @param ids {@link String} 报警ID集合，英文“,”分隔
     * @return 报警设备个数
     */
    Integer countAlarmDeviceByIds(String ids);

    /**
     * 根据报警ID查询同位置设备同类型报警的报警ID
     *
     * @param id {@link Long} 报警ID
     * @return 同位置设备同类型报警的报警ID
     */
    List<Long> findSameAlarmIdsById(Long id);

    /**
     * 添加消除报警记录
     *
     * @param eAlarmMap {@link Map} 消除报警信息集合<br>
     *                  id {@link Long} 报警ID<br>
     *                  userId {@link Long} 操作员ID<br>
     *                  app {@link Integer} 是否手机app<br>
     *                  lnt {@link Float} 手机定位经度<br>
     *                  lat {@link Float} 手机定位纬度<br>
     */
    void addEliminateAlarm(Map<String, Object> eAlarmMap);

    /**
     * 更新消除报警状态
     *
     * @param eliminateId     {@link Integer} 报警消除ID
     * @param eliminateStatus {@link Integer} 消除报警状态<br>
     *                        0：未消除<br>
     *                        1：报警消除请求中<br>
     *                        2：报警消除完成
     */
    void updateEliminateStatus(@Param("eliminateId") Integer eliminateId, @Param("eliminateStatus") Integer eliminateStatus);

    /**
     * 更新远程消除报警完成
     *
     * @param eliminateId {@link Integer} 报警消除ID
     * @param alarmIds    {@link String} 报警ID集合
     */
    void updateEliminateAlarmDone(@Param("eliminateId") Integer eliminateId, @Param("alarmIds") String alarmIds);

    /**
     * 更新先前同位置设备同类型报警的报警状态为远程消除
     *
     * @param eliminateId {@link Integer} 报警消除ID
     * @param alarmIdList {@link Long} 报警ID集合
     */
    void updateAlarmEliminated(@Param("eliminateId") Integer eliminateId, @Param("alarmIdList") List<Long> alarmIdList);

    /**
     * 获取未消除的报警信息
     *
     * @return 未消除的报警信息
     */
    List<AlarmRecord> findNotElimited();

    /**
     * 获取未消除的报警设备
     *
     * @return 报警设备
     */
    List<AlarmDevice> findNotElimitedAlarmDevice();

    /**
     * 根据报警设备获取报警信息
     *
     * @param alarmDevice 报警设备
     * @return 报警信息
     */
    AlarmInfo getAlarmInfoByAlarmDevcie(AlarmDevice alarmDevice);

    /**
     * 根据报警设备获取报警信息
     *
     * @param alarmId 报警ID
     * @return 报警信息
     */
    AlarmInfo getAlarmInfoByAlarmId(Long alarmId);

    /**
     * 获取未消除的报警信息(按数据库字段名称)
     *
     * @return 未消除的报警信息
     */
    List<Map<String, Object>> findNotElimitedForApp();

    /**
     * 按车辆查询报警记录（包括已消除）
     *
     * @param carId     车辆ID
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 报警记录
     */
    List<Map<String, Object>> findAlarmRecords(@Param("carId") Long carId,
                                               @Param("beginTime") String beginTime,
                                               @Param("endTime") String endTime);

    /**
     * 更新报警状态：4 设备已无效
     */
    Long updateAlarmStateForInvalidDevice();
}