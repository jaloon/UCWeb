package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.User;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.CenterConst;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.service.DeviceService;
import com.tipray.service.InfoManageLogService;
import com.tipray.util.JSONUtil;
import com.tipray.util.OkHttpUtil;
import com.tipray.util.OperateLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设备管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-10-22
 */
@Controller
@RequestMapping("/manage/device")
/* @Scope("prototype") */
public class DeviceController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Resource
    private DeviceService deviceService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        logger.info("dispatch device edit page, mode={}, id={}", mode, id);
        modelMap.put("mode", mode);
        Device device = new Device();
        if (id != null && id > 0) {
            device = deviceService.getDeviceById(id);
        }
        modelMap.put("device", device);
        return "normal/device/deviceEdit.jsp";
    }

    @PermissionAnno("deviceModule")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addDevice(@ModelAttribute Device device) {
        logger.info("add device, device={}", device);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DEVICE
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加设备：").append(device.getDeviceId()).append('。');
        try {
            deviceService.addDevice(device);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加设备异常：device={}, e={}", device, e.toString());
            logger.debug("添加设备异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("deviceModule")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateDevice(@ModelAttribute Device device) {
        logger.info("update device, device={}", device);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DEVICE
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改设备：").append(device.getDeviceId()).append('。');
        try {
            deviceService.updateDevice(device);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改设备异常：device={}, e={}", device, e.toString());
            logger.debug("修改设备异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("deviceModule")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteDevice(Long id) {
        logger.info("delete device, id={}", id);
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DEVICE
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除设备：");
        try {
            deviceService.deleteDeviceById(id);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除设备异常：id={}, e={}", id, e.toString());
            logger.debug("删除设备异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping(value = "getByDeviceId.do")
    @ResponseBody
    public Device getDeviceByDeviceId(Integer deviceId) {
        logger.info("get device by device_id, deviceId={}", deviceId);
        return deviceService.getDeviceByDeviceId(deviceId);
    }

    @RequestMapping(value = "findByType.do")
    @ResponseBody
    public List<Device> findDevicesByType(Integer deviceType) {
        logger.info("find devices by type, deviceType={}", deviceType);
        return deviceService.findByType(deviceType);
    }

    @RequestMapping(value = "isExist.do")
    @ResponseBody
    public Boolean isDeviceExist(Integer deviceId) {
        logger.info("device exist, deviceId={}", deviceId);
        return deviceService.getDeviceByDeviceId(deviceId) != null;
    }

    @PermissionAnno("viewDevice")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<Device> ajaxFindDevicesForPage(@ModelAttribute Device device, @ModelAttribute Page page) {
        logger.info("device list page, device={}, page={}", device, page);
        GridPage<Device> gridPage = deviceService.findDeviceForPage(device, page);
        return gridPage;
    }

    @PermissionAnno("syncDevice")
    @RequestMapping(value = "sync.do")
    @ResponseBody
    public Message sync() {
        User user = ThreadVariable.getUser();
        InfoManageLog infoManageLog = new InfoManageLog(user);
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_DEVICE
                | LogTypeConst.TYPE_SYNC | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("同步");
        try {
            description.append(CenterConst.CENTER_NAME).append("设备，");
            String url = new StringBuffer(CenterConst.PLTONE_URL).append("/api/deviceSync.do?id=")
                    .append(CenterConst.CENTER_ID).toString();
            String json = OkHttpUtil.get(url);
            List<Device> devices = JSONUtil.parseToList(json, Device.class);
            deviceService.sync(devices);
            description.append("成功！");
            logger.info("操作员：{}（{}），同步设备成功！", user.getName(), user.getAccount());
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("操作员：{}（{}），同步设备异常：e={}", user.getName(), user.getAccount(), e.toString());
            logger.debug("同步设备异常堆栈信息：", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }
}
