package com.tipray.dao;

import com.tipray.bean.track.LastTrack;
import com.tipray.bean.track.TrackInfo;
import com.tipray.core.annotation.MyBatisAnno;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TrackDao
 *
 * @author chenlong
 * @version 1.0 2018-05-31
 */
@MyBatisAnno
public interface TrackDao {

    /**
     * 根据轨迹ID查询轨迹
     *
     * @param trackIds {@link String} 轨迹ID，逗号分隔
     * @return {@link TrackInfo} 轨迹信息
     */
    List<TrackInfo> findTracksByTrackIds(String trackIds);

    /**
     * 根据车辆ID和轨迹开始时间查询轨迹
     *
     * @param carIds    {@link String} 车辆ID，逗号分隔
     * @param beginTime {@link String} 轨迹开始时间
     * @return {@link TrackInfo} 轨迹信息
     */
    List<TrackInfo> findTracksByCarIdsAndBeginTime(@Param("carIds") String carIds,
                                                   @Param("beginTime") String beginTime);

    /**
     * 根据车辆ID和轨迹所处时间段查询轨迹
     *
     * @param carId     {@link Long} 车辆ID
     * @param beginTime {@link String} 轨迹开始时间
     * @param endTime   {@link String} 轨迹结束时间
     * @return {@link TrackInfo} 轨迹信息
     */
    List<TrackInfo> findTracksByCarIdAndTimeRange(@Param("carId") Long carId,
                                                  @Param("beginTime") String beginTime,
                                                  @Param("endTime") String endTime);

    /**
     * 查询最新在线车辆轨迹
     *
     * @return {@link LastTrack} 最新轨迹信息
     */
    List<LastTrack> findLastTracks();
}
