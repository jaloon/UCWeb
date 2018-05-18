package com.tipray.util;

import com.tipray.bean.ResponseMsg;
import com.tipray.constant.reply.*;
import com.tipray.net.constant.UdpProtocolParseResultEnum;

/**
 * Response信息工具类
 * 
 * @author chenlong
 * @version 1.0 2018-05-03
 *
 */
public class ResponseMsgUtil {
	/** 成功ID */
	private static final int ID_SUCCESS = 0;
	/** 错误ID */
	private static final int ID_ERROR = 1;
	/** 异常ID */
	private static final int ID_EXCEPTON = 2;
	/** 成功结果 */
	public static final String SUCCESS = "success";
	/** 错误或异常结果 */
	public static final String ERROR = "error";

	/**
	 * 成功回复（无附加信息）
	 * 
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg success() {
		return success("");
	}

	/**
	 * 成功回复（有附加信息）
	 * 
	 * @param msg
	 *            {@link Object} 附加信息
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg success(Object msg) {
		return new ResponseMsg(ID_SUCCESS, SUCCESS, ErrorTagConst.NO_ERROR_TAG, 0, msg);
	}

	/**
	 * 错误回复
	 * 
	 * @param errorTag
	 *            {@link Byte} 错误标志
	 * @param errorCode
	 *            {@link Integer} 错误码
	 * @param errorMsg
	 *            {@link Object} 错误信息
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(byte errorTag, int errorCode, Object errorMsg) {
		return new ResponseMsg(ID_ERROR, ERROR, errorTag, errorCode, errorMsg);
	}

    /**
     * 错误回复（道闸接口错误）
     *
     * @param barrierError
     *            {@link BarrierErrorEnum} 道闸接口错误
     * @return {@link ResponseMsg}
     */
    public static ResponseMsg error(BarrierErrorEnum barrierError) {
	    return error(ErrorTagConst.BARRIER_ERROR_TAG, barrierError.code(), barrierError.msg());
    }

	/**
	 * 错误回复（设备绑定错误）
	 * 
	 * @param devBindError
	 *            {@link DevBindErrorEnum} 设备绑定错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(DevBindErrorEnum devBindError) {
		return error(ErrorTagConst.DEV_BIND_ERROR_TAG, devBindError.code(), devBindError.msg());
	}

	/**
	 * 错误回复（根据车牌号获取车辆轨迹错误）
	 * 
	 * @param devBindError
	 *            {@link FindTracksByCarNumberErrorEnum} 根据车牌号获取车辆轨迹错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(FindTracksByCarNumberErrorEnum findTracksError) {
		return error(ErrorTagConst.FIND_TRACK_ERROR_TAG, findTracksError.code(), findTracksError.msg());
	}

	/**
	 * 错误回复（权限错误）
	 * 
	 * @param devBindError
	 *            {@link PermissionErrorEnum} 权限错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(PermissionErrorEnum permissionError) {
		return error(ErrorTagConst.PERMISSION_ERROR_TAG, permissionError.code(), permissionError.msg());
	}

	/**
	 * 错误回复（远程换站错误）
	 * 
	 * @param devBindError
	 *            {@link RemoteChangeErrorEnum} 远程换站错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(RemoteChangeErrorEnum remoteChangeError) {
		return error(ErrorTagConst.CHANGE_STATION_ERROR_TAG, remoteChangeError.code(), remoteChangeError.msg());
	}

	/**
	 * 错误回复（远程操作错误）
	 * 
	 * @param devBindError
	 *            {@link RemoteControlErrorEnum} 远程操作错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(RemoteControlErrorEnum remoteControlError) {
		return error(ErrorTagConst.ROMOTE_CONTROL_ERROR_TAG, remoteControlError.code(), remoteControlError.msg());
	}

	/**
	 * 错误回复（远程消除报警错误）
	 * 
	 * @param devBindError
	 *            {@link RemoteEliminateAlarmErrorEnum} 远程消除报警错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(RemoteEliminateAlarmErrorEnum eliminateAlarmError) {
		return error(ErrorTagConst.ELIMINATE_ALARM_ERROR_TAG, eliminateAlarmError.code(), eliminateAlarmError.msg());
	}

	/**
	 * 错误回复（远程开锁重置错误）
	 * 
	 * @param devBindError
	 *            {@link RemoteLockResetErrorEnum} 远程开锁重置错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(RemoteLockResetErrorEnum lockResetError) {
		return error(ErrorTagConst.LOCK_OPEN_RESET_ERROR_TAG, lockResetError.code(), lockResetError.msg());
	}

	/**
	 * 错误回复（车台配置更新错误）
	 * 
	 * @param devBindError
	 *            {@link TerminalConfigUpdateErrorEnum} 车台配置更新错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(TerminalConfigUpdateErrorEnum terminalConfigError) {
		return error(ErrorTagConst.TERMINAL_CONFIG_ERROR_TAG, terminalConfigError.code(), terminalConfigError.msg());
	}

	/**
	 * 错误回复（车台软件升级错误）
	 * 
	 * @param devBindError
	 *            {@link TerminalSoftwareUpgradeErrorEnum} 车台软件升级错误
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(TerminalSoftwareUpgradeErrorEnum terminalUpgradeError) {
		return error(ErrorTagConst.TERMINAL_UPGRADE_ERROR_TAG, terminalUpgradeError.code(), terminalUpgradeError.msg());
	}

	/**
	 * 错误回复（UDP协议解析结果）
	 * 
	 * @param devBindError
	 *            {@link UdpProtocolParseResultEnum} UDP协议解析结果
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg error(UdpProtocolParseResultEnum protocolParseResult) {
		return error(ErrorTagConst.UDP_PARSE_ERROR_TAG, protocolParseResult.code(), protocolParseResult.msg());
	}

	/**
	 * 异常回复
	 * 
	 * @param e
	 *            {@link Exception} 异常
	 * @return {@link ResponseMsg}
	 */
	public static ResponseMsg excetion(Exception e) {
		return new ResponseMsg(ID_EXCEPTON, ERROR, ErrorTagConst.EXCEPTON_MESSAGE_TAG, 10, e.getMessage());
	}

}
