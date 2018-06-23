package com.tipray.controller;

import com.github.crab2died.ExcelUtils;
import com.tipray.bean.Message;
import com.tipray.bean.baseinfo.GasStation;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.CenterConfigConst;
import com.tipray.constant.LogTypeConst;
import com.tipray.core.ThreadVariable;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.FileException;
import com.tipray.service.GasStationService;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.OilDepotService;
import com.tipray.service.VehicleService;
import com.tipray.util.OperateLogUtil;
import com.tipray.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * SpringMVC文件上传/下载控制器
 * 
 * @author chenlong
 * @version 1.0 2018-02-02
 *
 */
@Controller
@RequestMapping("/manage/file")
// @RequestMapping("/manage/file")
public class FileController extends BaseAction {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FileController.class);
	private static final int UPLOAD_DEPOT = 1;
	private static final int UPLOAD_STATON = 2;
	private static final int UPLOAD_VEHICLE = 3;
	@Resource
	private OilDepotService oilDepotService;
	@Resource
	private GasStationService gasStationService;
	@Resource
	private VehicleService vehicleService;
	@Resource
	private InfoManageLogService infoManageLogService;

	/**
	 * 多文件上传
	 * 
	 * @param uploadFiles
	 */
	@RequestMapping(value = "multiUpload", method = RequestMethod.POST)
	@ResponseBody
	public void uploadFiles(@RequestParam MultipartFile[] uploadFiles) {
		for (MultipartFile item : uploadFiles) {
			saveFileToServer(item);
		}
	}

	/**
	 * 单文件上传
	 * 
	 * @param uploadFile
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
	public Message upload(MultipartFile uploadFile, Integer biz) {
		if (uploadFile == null) {
		    logger.error("文件上传失败！");
			return Message.error("文件上传失败！");
		}
		if (biz == null) {
            logger.error("参数未指定！biz=null");
			return Message.error("参数未指定！");
		}
		if (biz < 1 || biz > 3) {
            logger.error("参数超出规定范围！！biz={}", biz);
            return Message.error("参数超出规定范围！");
        }
		boolean result = false;
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
		Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.TYPE_BATCH_IMPORT | LogTypeConst.RESULT_DONE;
		StringBuffer description = new StringBuffer("批量导入：");
		try (InputStream inputStream = uploadFile.getInputStream()) {
			switch (biz) {
				case UPLOAD_DEPOT:
                    type |= LogTypeConst.ENTITY_OIL_DEPOT;
					description.append("油库。");
					List<OilDepot> oilDepots = ExcelUtils.getInstance().readExcel2Objects(inputStream, OilDepot.class);
					oilDepotService.addOilDepots(oilDepots);
					break;
				case UPLOAD_STATON:
                    type |= LogTypeConst.ENTITY_GAS_STATION;
					description.append("加油站。");
					List<GasStation> gasStations = ExcelUtils.getInstance().readExcel2Objects(inputStream, GasStation.class);
					gasStationService.addGasStations(gasStations);
					break;
				case UPLOAD_VEHICLE:
                    type |= LogTypeConst.ENTITY_VEHICLE;
					description.append("车辆。");
					List<Vehicle> vehicles = ExcelUtils.getInstance().readExcel2Objects(inputStream, Vehicle.class);
					vehicleService.addCars(vehicles);
					break;
				default:
					break;
			}
			description.append("成功。");
			logger.info("批量导入{}成功！", biz == 1 ? "油库" : biz == 2 ? "加油站" : "车辆");
			result = true;
		} catch (Exception e) {
			type++;
			description.append("失败。");
			logger.error("批量导入{}异常：e={}", biz == 1 ? "油库" : biz == 2 ? "加油站" : "车辆", e.toString());
			logger.debug("批量导入异常堆栈信息：", e);
		} finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
		}
		saveFileToServer(uploadFile);
		if (result) {
			return Message.success();
		} else {
			return Message.error("批量导入失败！");
		}
	}

	/**
	 * 把用户上传的文件保存到服务器
	 * 
	 * @param uploadFile
	 *            {@link MultipartFile}
	 */
	private void saveFileToServer(MultipartFile uploadFile) {
		if (uploadFile.getSize() > 0) {
			String filename = uploadFile.getOriginalFilename();
			File destFile = new File(CenterConfigConst.EXCEL_FILE_PATH, filename);
			try {
				uploadFile.transferTo(destFile);
			} catch (Exception e) {
				logger.error("save upload file error: e={}", e.toString());
				logger.debug("save upload file error stack info: ", e);
			}
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param filename
	 *            下载文件名
	 */
	// 此处必须写成value = "downloadUpgradeFile/{filename:.+}"。
	// 若写为value = "downloadUpgradeFile/{filename}"，则接收的参数filename若包含特殊字符会从特殊字符出截断
	@RequestMapping(value = "download/{filename:.+}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> download(@PathVariable(value = "filename") String filename) {
	    logger.info("downloadUpgradeFile file, filename={}", filename);
		try {
			if (StringUtil.isEmpty(filename)) {
			    logger.warn("file name is null!");
				throw new FileException(FileException.FILE_NAME_NULL_EXCEPTION);
			}
			// 服务器文件存放路径
			String downloadFilePath = new StringBuffer("d:/ftp/").append(filename).toString();
			File file = new File(downloadFilePath);
			if (!file.exists()) {
			    logger.warn("file is not existed!");
				throw new FileException(FileException.FILE_NOT_FOUND_EXCEPTION);
			}
			// http头信息
			HttpHeaders headers = new HttpHeaders();
			// Content-Disposition中filename的值可以设置为空字符串""或null，此时spring会对原文件名自动编码；
			// 否则下载文件名就是此处设置的filename的值，如果包含中文，浏览器的下载文件名会乱码，
			// 可以采用 filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1"); 对原文件名编码
			headers.setContentDispositionFormData("attachment", "");
			// MediaType:互联网媒介类型 contentType：具体请求中的媒体类型信息
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			// 设为HttpStatus.CREATED时，IE无法下载文件
			return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
		} catch (IOException e) {
			logger.error("文件下载失败！filename={}, e={}", filename, e.toString());
			logger.debug("文件下载异常堆栈信息：", e);
			throw new FileException(FileException.FILE_DOWNLOAD_EXCEPTION, e);
		}
	}

}
