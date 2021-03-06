package com.tipray.dao;

import com.tipray.bean.Station;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * GasStationDao
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@MyBatisAnno
public interface GasStationDao extends BaseDao<GasStation> {
    /**
     * 根据加油站id集合批量删除
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据编号或名称或简称统计有效加油站
     *
     * @param gasStation 加油站
     * @return 加油站数目
     */
    Integer countValidGasStation(GasStation gasStation);

    /**
     * 根据编号统计有效加油站
     *
     * @param officialId 加油站编号
     * @retur 加油站数目
     */
    Integer countValidGasStationByOfficialId(String officialId);

    /**
     * 根据名称统计有效加油站
     *
     * @param name 加油站名称
     * @return 加油站数目
     */
    Integer countValidGasStationByName(String name);

    /**
     * 根据简称统计有效加油站
     *
     * @param abbr 加油站简称
     * @return 加油站数目
     */
    Integer countValidGasStationByAbbr(String abbr);

    /**
     * 根据编号或名称或简称查询无效加油站ID
     *
     * @param gasStation 加油站
     * @return 加油站ID
     */
    List<Long> findInvalidGasStation(GasStation gasStation);

    /**
     * 根据加油站名称获取加油站列表
     *
     * @param oildepotName
     * @return
     */
    List<GasStation> findByName(String gasStationName);

    /**
     * 根据加油站名称获取加油站信息
     *
     * @param gasStationName
     * @return
     */
    GasStation getByName(String gasStationName);

    /**
     * 根据加油站编号获取加油站信息
     *
     * @param officialId
     * @return
     */
    GasStation getByOfficialId(String officialId);

    /**
     * 根据加油站ID删除与该加油站有关的所有卡信息
     *
     * @param id
     */
    void deleteGasStationCardsById(Long id);

    /**
     * 根据加油站ID和卡ID添加与该加油站有关的卡信息
     *
     * @param map
     */
    void addGasStationCardsByIdAndCardIds(List<Map<String, Object>> list);

    /**
     * 根据卡ID查询所属加油站信息
     *
     * @param cardId
     * @return
     */
    List<GasStation> findGasStationsByCardId(Long cardId);

    /**
     * 获取加油站列表
     *
     * @return
     */
    List<GasStation> getGasStationList();

    /**
     * 查询未配置手持机的加油站
     *
     * @return
     */
    List<GasStation> findUnconfigGasStation();

    /**
     * 根据加油站ID查询换站相关加油站信息
     *
     * @param id 加油站ID
     * @return 加油站经纬度、施解封半径、占地范围、手持机设备ID
     */
    Map<String, Object> getStationForChangeById(long id);

    /**
     * 根据加油站官方编号查询换站相关加油站信息
     *
     * @param officialId 加油站官方编号
     * @return 加油站经纬度、施解封半径、占地范围、手持机设备ID
     */
    Map<String, Object> getStationForChangeByOfficialId(String officialId);

    /**
     * 根据加油站ID查询该加油站持有的普通卡
     *
     * @param id 加油站ID
     * @return 普通卡ID
     */
    List<Long> findOrdinaryCardById(long id);

    /**
     * 批量添加加油站
     *
     * @param gasStations
     */
    void addGasStations(List<GasStation> gasStations);

    /**
     * 根据指定数量获取最近添加的加油站列表
     *
     * @param size
     * @return
     */
    List<GasStation> findRecentGasStationsBySize(int size);

    /**
     * 获取所有加油站的ID和名称
     *
     * @return
     */
    List<Map<String, Object>> findIdAndNameOfAllGasStations();

    /**
     * h获取加油站信息
     *
     * @return
     */
    List<Station> findStationsForApp();
}
