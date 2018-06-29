package com.tipray.dao;

import com.tipray.bean.upgrade.TerminalUpgradeInfo;
import com.tipray.bean.upgrade.TerminalUpgradeRecord;
import com.tipray.bean.upgrade.UpgradeCancelVehicle;
import com.tipray.core.annotation.MyBatisAnno;

import java.util.List;

/**
 * TerminalUpgradeDao
 *
 * @author chenlong
 * @version 1.0 2018-06-21
 */
@MyBatisAnno
public interface TerminalUpgradeDao {
    /**
     * 添加车台升级信息
     *
     * @param terminalUpgradeInfo 车台升级信息
     */
    void addTerminalUpgradeInfo(TerminalUpgradeInfo terminalUpgradeInfo);

    /**
     * 根据设备ID查找设备未完成更新的记录ID
     *
     * @param terminalId 设备ID
     * @return 未完成更新的记录ID集合
     */
    List<Long> findUnfinishedUpgradeByTerminalId(Integer terminalId);

    /**
     * 添加车台升级记录
     *
     * @param terminalUpgradeRecord
     */
    void addTerminalUpgradeRecord(TerminalUpgradeRecord terminalUpgradeRecord);

    /**
     * 更新车台升级记录
     *
     * @param terminalUpgradeRecord
     */
    void updateTerminalUpgradeRecord(TerminalUpgradeRecord terminalUpgradeRecord);

    /**
     * 批量删除车台升级记录
     *
     * @param ids 车台升级记录ID集合
     */
    void deleteTerminalUpgradeRecord(List<Long> ids);

    /**
     * 批量删除车台升级记录
     *
     * @param ids 车台升级记录ID，英文逗号“,”分隔
     */
    void batchDeleteUpgradeRecord(String ids);

    /**
     * 查询未完成升级的车辆信息
     *
     * @param carNumber 车牌号
     * @return 未完成升级的车辆信息
     */
    List<UpgradeCancelVehicle> findUnfinishUpgradeVehicles(String carNumber);

    /**
     * 根据升级记录ID获取升级状态
     *
     * @param upgradeRecordId 升级记录ID
     * @return 升级状态
     */
    Integer getUpgradeStatusById(Long upgradeRecordId);
}
