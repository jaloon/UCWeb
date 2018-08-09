package com.tipray.service;

import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.bean.record.AlarmRecord;
import com.tipray.core.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * AlarmRecordService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface AlarmRecordService extends RecordService<AlarmRecord> {
	/**
	 * 根据报警ID集合获取报警记录
	 *
	 * @param ids {@link String} 报警ID集合，英文“,”分隔
	 * @param vehicleId {@link Long} 车辆ID
	 * @return
	 */
	List<AlarmRecord> getAlarmRecordsByIdsAndCar(String ids, Long vehicleId);

    /**
     * 根据报警ID集合获取报警设备个数
     *
     * @param ids {@link String} 报警ID集合，英文“,”分隔
     * @return
     */
    Integer countAlarmDeviceByIds(String ids);

    /**
     * 根据报警ID查询同位置设备同类型报警的报警ID
     * @param id {@link Long} 报警ID
     * @return 同位置设备同类型报警的报警ID
     */
    List<Long> findSameAlarmIdsById(Long id);

    /**
	 * 添加消除报警记录
	 * 
	 * @param eAlarmMap
	 *            {@link Map} 消除报警信息集合<br>
	 *            userId {@link Long} 操作员ID<br>
	 *            vehicleId {@link Long} 车辆ID<br>
	 *            alarmIds {@link byte[]} 报警ID集合（字节数组）<br>
	 *            app {@link Integer} 是否手机app<br>
	 *            lnt {@link Float} 手机定位经度<br>
	 *            lat {@link Float} 手机定位纬度<br>
	 *            isLocationValid {@link Integer} 手机定位是否有效
	 * @throws ServiceException
	 */
    void addEliminateAlarm(Map<String, Object> eAlarmMap) throws ServiceException;

	/**
	 * 更新消除报警状态
	 * 
	 * @param eliminateId
	 *            {@link Integer} 报警消除ID
	 * @param eliminateStatus
	 *            {@link Integer} 消除报警状态<br>
	 *            0：未消除<br>
	 *            1：报警消除请求中<br>
	 *            2：报警消除完成
	 * @param alarmIds
	 *            {@link String} 远程消除的报警ID集合
	 * @param alarmIdList
	 *            {@link Long} 同位置设备同类报警的报警ID集合
	 * @throws ServiceException
	 */
    void updateEliminateAlarm(Integer eliminateId, Integer eliminateStatus, String alarmIds, List<Long> alarmIdList) throws ServiceException;

	/**
	 * 获取未消除的报警信息
	 * 
	 * @return {@link List} 未消除的报警信息
	 */
    List<AlarmRecord> findNotElimited();

    /**
     * 获取未消除的报警信息
     * @return
     */
    List<AlarmInfo> findNotElimitedAlarmInfo();

	/**
	 * 获取未消除的报警信息(按数据库字段名称)
	 * 
	 * @return
	 */
    List<Map<String, Object>> findNotElimitedForApp();
}
