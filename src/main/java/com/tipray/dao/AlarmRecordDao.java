package com.tipray.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.record.AlarmRecord;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * AlarmRecordDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface AlarmRecordDao extends BaseDao<AlarmRecord> {
    /**
     * 根据报警ID集合获取报警记录
     *
     * @param ids {@link String} 报警ID集合，英文“,”分隔
     * @param vehicleId {@link Long} 车辆ID
     * @return
     */
    List<AlarmRecord> getAlarmRecordsByIdsAndCar(@Param("ids") String ids, @Param("vehicleId") Long vehicleId);

    /**
     * 根据报警ID集合获取报警设备个数
     *
     * @param ids {@link String} 报警ID集合，英文“,”分隔
     * @return
     */
    Integer countAlarmDeviceByIds(String ids);

	/**
	 * 添加消除报警记录
	 * 
	 * @param eAlarmMap
	 *            {@link Map} 消除报警信息集合<br>
	 *            id {@link Long} 报警ID<br>
	 *            userId {@link Long} 操作员ID<br>
	 *            app {@link Integer} 是否手机app<br>
	 *            lnt {@link Float} 手机定位经度<br>
	 *            lat {@link Float} 手机定位纬度<br>
	 */
    void addEliminateAlarm(Map<String, Object> eAlarmMap);

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
	 */
    void updateEliminateStatus(@Param("eliminateId") Integer eliminateId, @Param("eliminateStatus") Integer eliminateStatus);

    /**
     * 更新远程消除报警完成
     *
     * @param eliminateId
     *            {@link Integer} 报警消除ID
     * @param alarmIds
     *            {@link String} 报警ID集合，英文“,”分隔
     */
    void updateEliminateAlarmDone(@Param("eliminateId") Integer eliminateId, @Param("alarmIds") String alarmIds);

	/**
	 * 获取未消除的报警信息
	 * 
	 * @return {@link List} 未消除的报警信息
	 */
    List<AlarmRecord> findNotElimited();

	/**
	 * 获取未消除的报警信息(按数据库字段名称)
	 * 
	 * @return
	 */
    List<Map<String, Object>> findNotElimitedForApp();

}
