package com.tipray.controller;

import com.tipray.bean.ChangeInfo;
import com.tipray.bean.DropdownData;
import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.ResponseMsg;
import com.tipray.bean.VehicleStatus;
import com.tipray.bean.VehicleTerminalConfig;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.baseinfo.TransCompany;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.bean.track.ReTrack;
import com.tipray.bean.upgrade.UpgradeCancelVehicle;
import com.tipray.bean.upgrade.VehicleTree;
import com.tipray.constant.LogTypeConst;
import com.tipray.constant.RemoteControlConst;
import com.tipray.constant.reply.FindTracksByCarNumberErrorEnum;
import com.tipray.constant.reply.PermissionErrorEnum;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.PermissionException;
import com.tipray.service.GasStationService;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.OilDepotService;
import com.tipray.service.VehicleService;
import com.tipray.util.AccreditUtil;
import com.tipray.util.DateUtil;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.JSONUtil;
import com.tipray.util.OperateLogUtil;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-11-22
 */
@Controller
@RequestMapping("/manage/car")
public class VehicleController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);
    @Resource
    private VehicleService vehicleService;
    @Resource
    private OilDepotService oilDepotService;
    @Resource
    private GasStationService gasStationService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("carModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch car edit page, mode={}, id={}", mode, id);
        }
        modelMap.put("mode", mode);
        if (id != null && id > 0) {
            Map<String, Object> map = new HashMap<>(16);
            map = vehicleService.getCarById(id);
            modelMap.put("car", map.get("car"));
            modelMap.put("drivers", map.get("drivers"));
            modelMap.put("locks", map.get("locks"));
        }
        return "normal/car/carEdit.jsp";
    }

    @PermissionAnno("editCar")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addCar(@ModelAttribute Vehicle car, String driverIds) {
        if (logger.isDebugEnabled()) {
            logger.debug("add car, car={}, driverIds={}", car, driverIds);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_VEHICLE
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加车辆：").append(car.getCarNumber()).append('。');
        try {
            vehicleService.addCar(car, driverIds);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加车辆异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editCar")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateCar(@ModelAttribute Vehicle car, String driverIds, String locksJson) {
        if (logger.isDebugEnabled()) {
            logger.debug("update car, car={}, driverIds={}, locksJson={}", car, driverIds, locksJson);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_VEHICLE
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改车辆：").append(car.getCarNumber()).append('。');
        try {
            List<Lock> locks = JSONUtil.parseToList(locksJson, Lock.class);
            vehicleService.updateCar(car, driverIds, locks);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改车辆异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editCar")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteCar(Long id, String carNumber) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete car, id={}, carNumber={}", id, carNumber);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_VEHICLE
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除车辆：").append(carNumber).append('。');
        try {
            vehicleService.deleteCar(id, carNumber);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除车辆异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("viewCar")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<Vehicle> ajaxFindCarsForPage(@ModelAttribute Vehicle car, @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("car list page, car={}, page={}", car, page);
        }
        User user = ThreadVariable.getUser();
        Long userComId = user.getComId();
        TransCompany transCompany = car.getTransCompany();
        if (userComId > 0 && transCompany != null) {
            Long carComId = transCompany.getId();
            if (carComId == null) {
                transCompany.setId(userComId);
                car.setTransCompany(transCompany);
            } else if (!userComId.equals(carComId)) {
                logger.warn("操作员{}({})属于运输公司【{}】，请求查询运输公司【{}】的车辆，权限不足！",
                        user.getAccount(), user.getName(), userComId, carComId);
                throw new PermissionException(PermissionErrorEnum.PERMISSION_DENIED);
            }
        }
        return vehicleService.findCarsForPage(car, page);
    }

    /**
     * 根据车牌号获取车辆信息
     *
     * @param carNo 车牌号
     * @return
     */
    @RequestMapping(value = "getCarByNo.do")
    @ResponseBody
    public Vehicle getCarByNo(String carNo) {
        if (logger.isDebugEnabled()) {
            logger.debug("get car by carno, carNo={}", carNo);
        }
        return vehicleService.getByCarNo(carNo);
    }

    /**
     * 根据车牌号获取车载终端设备ID
     *
     * @param carNo {@link String} 车牌号
     * @return {@link Integer} 车载终端设备ID
     */
    @RequestMapping(value = "getTerminalIdByCarNo.do")
    @ResponseBody
    public Integer getTerminalIdByCarNo(String carNo) {
        return vehicleService.getTerminalIdByCarNo(carNo);
    }

    /**
     * 根据车载终端ID获取车辆信息
     *
     * @param terminalId 车载终端ID
     * @return
     */
    @RequestMapping(value = "getCarByTerminalId.do")
    @ResponseBody
    public Vehicle getCarByTerminalId(Integer terminalId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get car by terminal id, terminalId={}", terminalId);
        }
        return vehicleService.getCarByTerminalId(terminalId);
    }

    /**
     * 获取所有的车辆信息列表
     *
     * @return
     */
    @RequestMapping(value = "getCarList.do")
    @ResponseBody
    public List<Vehicle> getCarList() {
        return vehicleService.findAllCars();
    }

    /**
     * 获取未使用的车台
     *
     * @return
     */
    @RequestMapping(value = "findUnusedTerminals.do")
    @ResponseBody
    public List<Device> findUnusedTerminals() {
        return vehicleService.findUnusedTerminal();
    }

    /**
     * 获取未使用的锁设备ID列表
     *
     * @return
     */
    @RequestMapping(value = "findUnusedLocks.do")
    @ResponseBody
    public List<Integer> findUnusedLocks() {
        return vehicleService.findUnusedLocks();
    }

    /**
     * 获取已绑定车载终端的车辆
     *
     * @return
     */
    @RequestMapping(value = "findBindedCars.do")
    @ResponseBody
    public List<Vehicle> findBindedCars() {
        return vehicleService.findBindedCars();
    }

    /**
     * 获取未绑定车载终端的车辆
     *
     * @return
     */
    @RequestMapping(value = "findUnbindedCars.do")
    @ResponseBody
    public List<Vehicle> findUnbindedCars() {
        return vehicleService.findUnbindedCars();
    }

    /**
     * 根据车辆ID获取绑定的锁列表
     *
     * @param carId
     * @return
     */
    @RequestMapping(value = "findLocksByCarId.do")
    @ResponseBody
    public List<Lock> findLocksByCarId(Long carId) {
        if (logger.isDebugEnabled()) {
            logger.debug("find locks by car id, carId={}", carId);
        }
        return vehicleService.findLocksByCarId(carId);
    }

    /**
     * 根据车牌号获取待绑定锁设备ID列表
     *
     * @param carNumber 车牌号
     * @return
     */
    @RequestMapping(value = "getBindingLocks.do")
    @ResponseBody
    public List<Integer> getBindingLocks(String carNumber) {
        if (logger.isDebugEnabled()) {
            logger.debug("get binding locks, carNumber={}", carNumber);
        }
        return vehicleService.findBindingLockDeviceIds(carNumber);
    }

    /**
     * 远程操作转发控制
     *
     * @param mode      远程操作类型（1：进油库 | 2：出油库 | 3：进加油站 | 4：出加油站 | 5 进油区 | 6 出油区 | 7：状态强制变更 ）
     * @param carNumber 车牌号
     * @param modelMap
     * @return
     */
    @PermissionAnno("remoteModule")
    @RequestMapping(value = "carStatusDispatch.do")
    public String carStatusDispatch(Integer mode, String carNumber, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch remote control page, mode={}, carNumber={}", mode, carNumber);
        }
        modelMap.put("mode", mode);
        VehicleStatus carStatus = vehicleService.getCarStatus(carNumber);
        if (carStatus.getStatus() == null) {
            // carStatus.setOnline(0);
            carStatus.setStatus(0);
            carStatus.setStatusName("未知");
        }
        modelMap.put("carStatus", carStatus);
        if (mode == RemoteControlConst.REMOTE_TYPE_1_INTO_DEPOT
                || mode == RemoteControlConst.REMOTE_TYPE_2_QUIT_DEPOT
                || mode == RemoteControlConst.REMOTE_TYPE_8_WAIT_OILDOM
                || mode == RemoteControlConst.REMOTE_TYPE_9_INTO_OILDOM
                || mode == RemoteControlConst.REMOTE_TYPE_10_QUIT_OILDOM) {
            List<Map<String, Object>> depots = oilDepotService.getIdAndNameOfAllOilDepots();
            modelMap.put("depots", depots);
        } else if (mode == RemoteControlConst.REMOTE_TYPE_3_INTO_STATION
                || mode == RemoteControlConst.REMOTE_TYPE_4_QUIT_STATION) {
            List<Map<String, Object>> stations = gasStationService.getIdAndNameOfAllGasStations();
            modelMap.put("stations", stations);
        } else if (mode == RemoteControlConst.REMOTE_TYPE_7_ALTER_STATUS) {
            Integer storeNum = vehicleService.getStoreNumByCarNo(carNumber);
            modelMap.put("storeNum", storeNum);
        }
        return "normal/car/remote/carRemote.jsp";
    }

    @RequestMapping(value = "getDistribution.do")
    @ResponseBody
    public List<ChangeInfo> getDistribution(String carNumber) {
        if (logger.isDebugEnabled()) {
            logger.debug("get distribution, carNumber={}", carNumber);
        }
        return vehicleService.getDistributionsByCarNumber(carNumber);
    }

    @PermissionAnno("changeModule")
    @RequestMapping(value = "changeDispatch.do")
    public String changeDispatch(String carNumber, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch change station page, carNumber={}", carNumber);
        }
        long userId = ThreadVariable.getUser().getId();
        List<ChangeInfo> distributions = vehicleService.getDistributionsByCarNumber(carNumber);
        List<Map<String, Object>> gasStations = gasStationService.getIdAndNameOfAllGasStations();
        modelMap.put("userId", userId);
        modelMap.put("carNumber", carNumber);
        modelMap.put("distributions", distributions);
        modelMap.put("gasStations", gasStations);
        return "normal/car/stationChange.jsp";
    }

    /**
     * 获取回放轨迹
     *
     * @param carTrack
     * @return 回放轨迹列表
     */
    @RequestMapping(value = "retrack.do")
    @ResponseBody
    public Map<String, Object> reTrack(ReTrack carTrack) {
        if (logger.isDebugEnabled()) {
            logger.debug("retrack, carTrack={}", carTrack);
        }
        return vehicleService.findTracks(carTrack);
    }

    /**
     * 根据车牌号获取车、司机相关信息
     *
     * @param carNumber 车牌号
     * @return
     */
    @RequestMapping(value = "getCarAndDriverByCarNo.do")
    @ResponseBody
    public Map<String, Object> getCarAndDriverByCarNo(String carNumber) {
        if (logger.isDebugEnabled()) {
            logger.debug("getCarAndDriverByCarNo: 根据车牌号获取车、锁相关信息, carNumber={}", carNumber);
        }
        if (StringUtil.isEmpty(carNumber)) {
            return null;
        }
        return vehicleService.getCarAndDriverByCarNo(carNumber);
    }

    /**
     * 根据车牌号获取锁及其状态信息
     *
     * @param carNumber 车牌号
     * @return
     */
    @RequestMapping(value = "findlocksByCarNo.do")
    @ResponseBody
    public List<Map<String, Object>> findlocksByCarNo(String carNumber) {
        if (logger.isDebugEnabled()) {
            logger.debug("findlocksByCarNo: 根据车牌号获取车、锁相关信息, carNumber={}", carNumber);
        }
        if (StringUtil.isEmpty(carNumber)) {
            return null;
        }
        return vehicleService.findlocksByCarNo(carNumber);
    }

    /**
     * 获取在线车辆信息
     *
     * @return
     */
    @RequestMapping(value = "findOnlineCars.do")
    @ResponseBody
    public List<Map<String, Object>> findOnlineCarsForApp() {
        return vehicleService.findOnlineCarsForApp();
    }

    /**
     * 获取所有车辆信息
     *
     * @return
     */
    @RequestMapping(value = "findAllCars.do")
    @ResponseBody
    public List<Map<String, Object>> findAllCarsForApp() {
        return vehicleService.findAllCarsForApp();
    }

    /**
     * 网页车辆选择控件数据获取接口
     *
     * @return 网页车辆选择控件数据
     */
    @RequestMapping(value = "selectDropdownData.do")
    @ResponseBody
    public List<DropdownData> selectDropdownData() {
        return vehicleService.selectCars();
    }

    /**
     * 网页车辆选择控件数据获取接口
     *
     * @param scope 选取范围（0 全部车辆，1 绑定车台的，2 在线）
     * @param comlimit 限制公司（默认限制，null/0 限制，1 不限）
     */
    @RequestMapping(value = "selectCars.do")
    @ResponseBody
    public Map<String, Object> selectCars(Integer scope, Integer comlimit) {
        Long comId = 0L;
        if (comlimit == null || comlimit == 0) {
            comId = ThreadVariable.getUser().getComId();
        }
        if (scope == null || scope == 0) {
            // 选取全部车辆
            return vehicleService.selectCars(0, comId);
        } else if (scope == 1) {
            // 只选取绑定了车台的车辆
            return vehicleService.selectCars(1, comId);
        } else if (scope == 2) {
            // 只选取在线车辆
            return vehicleService.selectCars(2, comId);
        }
        return null;
    }

    /**
     * 查询某【几】辆车某个时间点以后的轨迹信息
     *
     * @param carNumbers 车牌号，英文逗号“,”分隔
     * @param beginTime  轨迹开始时间
     * @return 轨迹信息
     */
    @RequestMapping(value = "findTracksByCarNumbers.do")
    @ResponseBody
    public ResponseMsg findTracksByCarNumbers(String carNumbers, String beginTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("findTracksByCarNumbers: 某【几】辆车某个时间点以后的轨迹信息, carNumbers={}, beginTime={}",
                    carNumbers, beginTime);
        }
        if (StringUtil.isEmpty(carNumbers)) {
            logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.CAR_NUMBER_NULL);
            return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.CAR_NUMBER_NULL);
        }
        if (StringUtil.isEmpty(beginTime)) {
            logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.BEGIN_TIME_NULL);
            return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.BEGIN_TIME_NULL);
        }
        try {
            Date beginDate = new SimpleDateFormat(DateUtil.FORMAT_DATETIME).parse(beginTime);
            long timeDiff = System.currentTimeMillis() - beginDate.getTime();
            if (timeDiff > DateUtil.DAY_DIFF) {
                logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.TIME_OUT_OF_SCOPE);
                return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.TIME_OUT_OF_SCOPE);
            }
        } catch (ParseException e) {
            logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.TIME_FORMAT_INVALID);
            return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.TIME_FORMAT_INVALID);
        }
        List<Map<String, Object>> list = vehicleService.findTracksByCarNumbers(carNumbers, beginTime);
        if (EmptyObjectUtil.isEmptyList(list)) {
            logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.TRACK_NULL);
            return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.TRACK_NULL);
        }
        return ResponseMsgUtil.success(list);
    }

    /**
     * 查询某辆车某个时间点以后的轨迹信息
     *
     * @param carNumber 车牌号
     * @param beginTime 轨迹开始时间
     * @return 轨迹信息
     */
    @RequestMapping(value = "findTracksByCarNumber.do")
    @ResponseBody
    public ResponseMsg findTracksByCarNumber(String carNumber, String beginTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("findTracksByCarNumber: 某辆车某个时间点以后的轨迹信息, carNumber={}, beginTime={}",
                    carNumber, beginTime);
        }
        if (StringUtil.isEmpty(carNumber)) {
            logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.CAR_NUMBER_NULL);
            return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.CAR_NUMBER_NULL);
        }

        Long carId = vehicleService.getIdByCarNo(carNumber);
        if (carId == null) {
            logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.VEHICLE_INVALID);
            return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.VEHICLE_INVALID);
        }
        Long beginMillis = null;
        if (!StringUtil.isEmpty(beginTime)) {
            try {
                Date beginDate = new SimpleDateFormat(DateUtil.FORMAT_DATETIME).parse(beginTime);
                beginMillis = beginDate.getTime();
            } catch (ParseException e) {
                logger.error("查询车辆轨迹信息错误：{}", FindTracksByCarNumberErrorEnum.TIME_FORMAT_INVALID);
                return ResponseMsgUtil.error(FindTracksByCarNumberErrorEnum.TIME_FORMAT_INVALID);
            }
        }
        return ResponseMsgUtil.success(vehicleService.findTracksByCarNumber(carNumber, carId, beginMillis));
    }

    /**
     * 获取已绑定车台的车辆的树形结构数据
     *
     * @return 辆的树形结构数据
     */
    @RequestMapping(value = "findBindedVehicleTree.do")
    @ResponseBody
    public List<VehicleTree> findBindedVehicleTree() {
        return vehicleService.findBindedVehicleTree();
    }

    /**
     * 获取车台已启用的功能
     *
     * @return 车台已启用的功能
     */
    @RequestMapping(value = "getFuncEnable.do")
    @ResponseBody
    public Integer getFuncEnable() {
        return vehicleService.getFuncEnable();
    }

    /**
     * 查询未完成升级的车辆信息
     *
     * @param carNumber 车牌号（为空时查询所有车辆）
     * @return 未完成升级的车辆信息
     */
    @RequestMapping(value = "findUnfinishUpgradeVehicles.do")
    @ResponseBody
    public List<UpgradeCancelVehicle> findUnfinishUpgradeVehicles(String carNumber) {
        return vehicleService.findUnfinishUpgradeVehicles(carNumber);
    }

    /**
     * 根据轨迹ID查询轨迹和锁信息
     * @param trackId 轨迹ID
     * @return 轨迹和锁信息
     */
    @RequestMapping(value = "getTrackAndLockInfoByTrackId.do")
    @ResponseBody
    public Map<String, Object> getTrackAndLockInfoByTrackId(String trackId) {
        return vehicleService.getTrackAndLockInfoByTrackId(trackId);
    }

    /**
     * 根据车牌号获取GPS配置信息
     * @param carNumber 车牌号
     * @return GPS配置信息
     */
    @RequestMapping(value = "getGpsConfByCarNumber.do")
    @ResponseBody
    public VehicleTerminalConfig getGpsConfByCarNumber(String carNumber) {
        return vehicleService.getGpsConfByCarNumber(carNumber);
    }

    /**
     * 根据授权码生成密码
     * @param authCode 授权码
     * @return 授权密码
     */
    @PermissionAnno("authCodeVerify")
    @RequestMapping(value = "getAuthPwd.do")
    @ResponseBody
    public ResponseMsg getAuthPwd(String authCode) {
        try {
            return ResponseMsgUtil.success(AccreditUtil.getAccreditPassword(authCode));
        }catch (Exception e) {
            logger.error(e.toString());
            return ResponseMsgUtil.exception(e);
        }
    }
}
