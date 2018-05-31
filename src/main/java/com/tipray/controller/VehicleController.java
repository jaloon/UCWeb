package com.tipray.controller;

import com.tipray.bean.*;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.constant.reply.FindTracksByCarNumberErrorEnum;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.service.GasStationService;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.OilDepotService;
import com.tipray.service.VehicleService;
import com.tipray.util.*;
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
/* @Scope("prototype") */
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

    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch car edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        Map<String, Object> map = new HashMap<>(16);
        if (id != null && id > 0) {
            map = vehicleService.getCarById(id);
        }
        modelMap.put("car", map.get("car"));
        modelMap.put("drivers", map.get("drivers"));
        modelMap.put("locks", map.get("locks"));
        return "normal/car/carEdit.jsp";
    }

    @PermissionAnno("carModule")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addCar(@ModelAttribute Vehicle car, String driverIds) {
        logger.info("add car, car={}, driverIds={}", car, driverIds);
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
            logger.error("添加车辆异常：car={}, e={}", car, e.toString());
            logger.debug("添加车辆异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("carModule")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateCar(@ModelAttribute Vehicle car, String driverIds) {
        logger.info("update car, car={}, driverIds={}", car, driverIds);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_VEHICLE
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改车辆：").append(car.getCarNumber()).append('。');
        try {
            vehicleService.updateCar(car, driverIds);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改车辆异常：car={}, e={}", car, e.toString());
            logger.debug("修改车辆异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("carModule")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteCar(Long id, String carNumber) {
        logger.info("delete car, id={}, carNumber={}", id, carNumber);
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
            logger.error("删除车辆异常：carNumber={}, e={}", carNumber, e.toString());
            logger.debug("删除车辆异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("carModule")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<Vehicle> ajaxFindCarsForPage(@ModelAttribute Vehicle car, @ModelAttribute Page page) {
        logger.info("car list page, car={}, page={}", car, page);
        GridPage<Vehicle> gridPage = vehicleService.findCarsForPage(car, page);
        return gridPage;
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
        logger.info("get car by carno, carNo={}", carNo);
        return vehicleService.getByCarNo(carNo);
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
        logger.info("get car by terminal id, terminalId={}", terminalId);
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
        logger.info("find locks by car id, carId={}", carId);
        return vehicleService.findLocksByCarId(carId);
    }

    /**
     * 根据车辆ID获取待绑定锁设备ID列表
     *
     * @param vehicleId 车辆ID
     * @return
     */
    @RequestMapping(value = "getBindingLocks.do")
    @ResponseBody
    public List<Integer> getBindingLocks(Long vehicleId) {
        logger.info("get binding locks, vehicleId={}", vehicleId);
        return vehicleService.findBindingLockDeviceIds(vehicleId);
    }

    /**
     * 远程操作转发控制
     *
     * @param mode      远程操作类型（1：进油库 | 2：出油库 | 3：进加油站 | 4：出加油站 | 5：远程状态变更）
     * @param carNumber 车牌号
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "carStatusDispatch.do")
    public String carStatusDispatch(Integer mode, String carNumber, ModelMap modelMap) {
        logger.info("dispatch remote control page, mode={}, carNumber={}", mode, carNumber);
        modelMap.put("mode", mode);
        VehicleStatus carStatus = vehicleService.getCarStatus(carNumber);
        if (carStatus.getStatus() == null) {
            carStatus.setOnline(0);
            carStatus.setStatus(0);
            carStatus.setStatusName("未知");
        }
        modelMap.put("carStatus", carStatus);
        if (mode < 3) {
            List<Map<String, Object>> depots = oilDepotService.getIdAndNameOfAllOilDepots();
            modelMap.put("depots", depots);
        } else if (mode < 5) {
            List<Map<String, Object>> stations = gasStationService.getIdAndNameOfAllGasStations();
            modelMap.put("stations", stations);
        }
        return "normal/car/remote/carRemote.jsp";
    }

    @RequestMapping(value = "getDistribution.do")
    @ResponseBody
    public List<ChangeInfo> getDistribution(String carNumber) {
        logger.info("get distribution, carNumber={}", carNumber);
        return vehicleService.getDistributionsByCarNumber(carNumber);
    }

    @RequestMapping(value = "changeDispatch.do")
    public String changeDispatch(String carNumber, ModelMap modelMap) {
        logger.info("dispatch change station page, carNumber={}", carNumber);
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
    public List<VehicleTrack> reTrack(VehicleTrack carTrack) {
        logger.info("retrack, carTrack={}", carTrack);
        List<VehicleTrack> list = vehicleService.findTracks(carTrack);
        return list;
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
        logger.info("getCarAndDriverByCarNo: 根据车牌号获取车、锁相关信息, carNumber={}", carNumber);
        if (StringUtil.isEmpty(carNumber)) {
            return null;
        }
        return vehicleService.getCarAndDriverByCarNo(carNumber);
    }

    /**
     * 根据车牌号获取锁及其状态信息
     * @param carNumber 车牌号
     * @return
     */
    @RequestMapping(value = "findlocksByCarNo.do")
    @ResponseBody
    public List<Map<String,Object>> findlocksByCarNo(String carNumber){
        logger.info("findlocksByCarNo: 根据车牌号获取车、锁相关信息, carNumber={}", carNumber);
        if (StringUtil.isEmpty(carNumber)) {
            return null;
        }
        return vehicleService.findlocksByCarNo(carNumber);
    }

    /**
     * 获取在线车辆信息
     * @return
     */
    @RequestMapping(value = "findOnlineCars.do")
    @ResponseBody
    public List<Map<String, Object>> findOnlineCarsForApp(){
        return vehicleService.findOnlineCarsForApp();
    }

    /**
     * 获取所有车辆信息
     * @return
     */
    @RequestMapping(value = "findAllCars.do")
    @ResponseBody
    public List<Map<String, Object>> findAllCarsForApp(){
        return vehicleService.findAllCarsForApp();
    }

    /**
     * 网页车辆选择控件数据获取接口
     *
     * @return
     */
    @RequestMapping(value = "selectDropdownData.do")
    @ResponseBody
    public List<DropdownData> selectDropdownData() {
        List<DropdownData> list = vehicleService.selectCars();
        return list;
    }

    /**
     * 网页车辆选择控件数据获取接口
     *
     * @param scope 选取范围（0 全部车辆，1 绑定车台的，2 在线）
     */
    @RequestMapping(value = "selectCars.do")
    @ResponseBody
    public Map<String, Object> selectCars(Integer scope) {

        if (scope == null || scope == 0) {
            // 选取全部车辆
            return vehicleService.selectCars(0);
        } else if (scope == 1) {
            // 只选取绑定了车台的车辆
            return vehicleService.selectCars(1);
        } else if (scope == 2){
            // 只选取在线车辆
            return vehicleService.selectCars(2);
        }
        return null;
    }

    /**
     * 查询某【几】辆车某个时间点以后的轨迹信息
     *
     * @param carNumbers 车牌号，英文逗号“,”分隔
     * @param beginTime  轨迹开始时间
     * @return
     */
    @RequestMapping(value = "findTracksByCarNumbers.do")
    @ResponseBody
    public ResponseMsg findTracksByCarNumbers(String carNumbers, String beginTime) {
        logger.info("findTracksByCarNumbers: 某【几】辆车某个时间点以后的轨迹信息, carNumbers={}, beginTime={}", carNumbers, beginTime);
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
     * 获取已绑定车台的车辆的树形结构数据
     *
     * @return
     */
    @RequestMapping(value = "findBindedVehicleTree.do")
    @ResponseBody
    public List<Map<String, Object>> findBindedVehicleTree() {
        return vehicleService.findBindedVehicleTree();
    }
}
