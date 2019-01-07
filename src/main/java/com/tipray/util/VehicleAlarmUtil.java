package com.tipray.util;

import com.tipray.bean.alarm.AlarmDevice;
import com.tipray.bean.alarm.AlarmInfo;
import com.tipray.constant.AlarmBitMarkConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆报警工具类
 *
 * @author chenlong
 * @version 1.0 2017-12-21
 * @see AlarmBitMarkConst
 */
public class VehicleAlarmUtil {
    // 车台报警信息位说明：以下位序从低位开始
    // ------------------------------------------------------------------------------
    // |8		|7 		|6 		|5 		|4 		|3 		|2 				|1 			|
    // ------------------------------------------------------------------------------
    // |保留		|保留 	|保留 	|保留 	|保留 	|保留 	|时钟电池报警 	|未施封越界 	|
    // ------------------------------------------------------------------------------

    // 锁报警信息位说明：以下位序从低位开始
    // ------------------------------------------------------------------------------------------
    // |8 			|7			|6		|5		    |4			|3 			|2 		|1			|
    // ------------------------------------------------------------------------------------------
    // |开关状态		|是否可用	|保留	|异常移动报警	|进入应急	|异常开锁	|低电压	|通讯异常	|
    // ------------------------------------------------------------------------------------------


    /**
     * 车辆是否报警
     *
     * @param TerminalAlarmStatus {@link Byte} 车台报警状态
     * @param lockStatusInfo      {@link byte[]} 锁状态信息流
     * @return
     */
    public static boolean isAlarm(byte TerminalAlarmStatus, byte[] lockStatusInfo) {
        if ((TerminalAlarmStatus & AlarmBitMarkConst.VALID_TERMINAL_ALARM_BITS) > 0) {
            return true;
        }
        return isLockAlarm(lockStatusInfo);
    }

    /**
     * 获取车台报警信息
     *
     * @param TerminalAlarmStatus {@link Byte} 车台报警状态
     * @return {@link String} 车台报警信息
     */
    public static String getTerminalAlarmInfo(byte TerminalAlarmStatus) {
        StringBuffer alarm = new StringBuffer();
        if ((TerminalAlarmStatus & AlarmBitMarkConst.LOCK_ALARM_BIT_1_COMM_ANOMALY) > 0) {
            alarm.append('、').append("未施封越界");
        }
        if ((TerminalAlarmStatus & AlarmBitMarkConst.LOCK_ALARM_BIT_2_LOW_BATTERY) > 0) {
            alarm.append('、').append("时钟电池报警");
        }
        if (alarm.length() > 0) {
            alarm.deleteCharAt(0);
        } else {
            alarm.append("无报警");
        }
        alarm.insert(0, "车载终端-");
        return alarm.toString();
    }

    /**
     * 获取锁报警信息
     *
     * @param lockStatusInfo
     * @return
     */
    public static String getLockAlarmInfo(byte[] lockStatusInfo) {
        if (lockStatusInfo != null && lockStatusInfo.length > 0) {
            StringBuffer alarm = new StringBuffer();
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                byte lock = lockStatusInfo[i];
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) == 0) {
                    continue;
                }
                alarm.append((i + 1) + "号锁：");
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_1_COMM_ANOMALY) > 0) {
                    alarm.append("通讯异常报警、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_2_LOW_BATTERY) > 0) {
                    alarm.append("电池低电压报警、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_3_UNUSUAL_UNLOCK) > 0) {
                    alarm.append("异常开锁报警、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_4_ENTER_URGENT) > 0) {
                    alarm.append("进入应急、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_5_UNUSUAL_MOVE) > 0) {
                    alarm.append("异常移动报警、");
                }
                if ((lock & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) == 0) {
                    alarm.append("无、");
                }
                alarm.setCharAt(alarm.length() - 1, '；');
            }
            if (alarm.length() > 0) {
                alarm.setCharAt(alarm.length() - 1, '。');
            }
            return alarm.toString();
        }
        return "-";
    }

    /**
     * 获取锁报警信息
     *
     * @param lockStatusInfo
     * @return
     */
    public static Map<Integer, String> getLockAlarmMap(byte[] lockStatusInfo) {
        if (lockStatusInfo != null && lockStatusInfo.length > 0) {
            Map<Integer, String> alarmMap = new HashMap<>();
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                StringBuffer alarm = new StringBuffer();
                byte lock = lockStatusInfo[i];
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) == 0) {
                    continue;
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_1_COMM_ANOMALY) > 0) {
                    alarm.append("通讯异常报警、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_2_LOW_BATTERY) > 0) {
                    alarm.append("电池低电压报警、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_3_UNUSUAL_UNLOCK) > 0) {
                    alarm.append("异常开锁报警、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_4_ENTER_URGENT) > 0) {
                    alarm.append("进入应急、");
                }
                if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_5_UNUSUAL_MOVE) > 0) {
                    alarm.append("异常移动报警、");
                }
                if ((lock & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) == 0) {
                    alarm.append("无报警、");
                }
                alarm.deleteCharAt(alarm.length() - 1);
                alarmMap.put(i + 1, alarm.toString());
            }
            return alarmMap;
        }
        return null;
    }

    /**
     * 根据锁序号获取锁报警信息
     *
     * @param lockStatusInfo
     * @param lockIndex
     * @return
     */
    public static String getLockAlarmByLockIndex(byte[] lockStatusInfo, Integer lockIndex) {
        if (lockStatusInfo == null || lockIndex > lockStatusInfo.length) {
            return "-";
        }
        byte lock = lockStatusInfo[lockIndex - 1];
        return getLockAlarm(lock);
    }

    /**
     * 获取锁报警信息
     *
     * @param lock
     * @return
     */
    public static String getLockAlarm(byte lock) {
        StringBuffer alarm = new StringBuffer();
        if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_1_COMM_ANOMALY) > 0) {
            alarm.append("、通讯异常报警");
        }
        if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_2_LOW_BATTERY) > 0) {
            alarm.append("、电池低电压报警");
        }
        if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_3_UNUSUAL_UNLOCK) > 0) {
            alarm.append("、异常开锁报警");
        }
        if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_4_ENTER_URGENT) > 0) {
            alarm.append("、进入应急");
        }
        if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_5_UNUSUAL_MOVE) > 0) {
            alarm.append("、异常移动报警");
        }
        if ((lock & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) == 0) {
            alarm.append("、无报警");
        }
        if (alarm.length() > 0) {
            alarm.deleteCharAt(0);
        }
        return alarm.toString();
    }

    /**
     * 获取锁报警状态值集合
     * @param lockInfo
     * @return
     */
    public static List<Integer> getLockAlarmValues(byte lockInfo) {
        List<Integer> list = new ArrayList<>();
        if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_1_COMM_ANOMALY) > 0) {
            list.add(1);
        }
        if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_2_LOW_BATTERY) > 0) {
            list.add(2);
        }
        if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_3_UNUSUAL_UNLOCK) > 0) {
            list.add(3);
        }
        if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_4_ENTER_URGENT) > 0) {
            list.add(4);
        }
        if ((lockInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_5_UNUSUAL_MOVE) > 0) {
            list.add(5);
        }
        return list;
    }

    /**
     * 是否报警
     *
     * @param lockStatusInfo
     * @return
     */
    public static boolean isLockAlarm(byte[] lockStatusInfo) {
        if (lockStatusInfo == null) {
            return false;
        }
        for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
            byte lock = lockStatusInfo[i];
            if ((lock & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) == 0) {
                // 无效锁提前排除
                continue;
            }
            if ((lock & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据锁序号获取锁报警状态
     *
     * @param lockStatusInfo
     * @param lockIndex
     * @return
     */
    public static boolean isLockAlarmByLockIndex(byte[] lockStatusInfo, Integer lockIndex) {
        byte lock = lockStatusInfo[lockIndex - 1];
        return (lock & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) > 0;
    }

    /**
     * 获取锁状态
     *
     * @param lockStatusInfo
     * @return
     */
    public static String getLockStatus(byte[] lockStatusInfo) {
        if (lockStatusInfo != null && lockStatusInfo.length > 0) {
            StringBuilder locks = new StringBuilder();
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                locks.append(i + 1).append("号锁：");
                int status = lockStatusInfo[i] & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
                locks.append(status == 0 ? '关' : '开').append('；');
            }
            locks.setCharAt(locks.length() - 1, '。');
            return locks.toString();
        }
        return "数据库记录异常。";
    }

    /**
     * 获取锁状态
     *
     * @param lockStatusInfo
     * @return
     */
    public static Map<Integer, String> getLockMap(byte[] lockStatusInfo) {
        if (lockStatusInfo != null && lockStatusInfo.length > 0) {
            Map<Integer, String> lockMap = new HashMap<>();
            for (int i = 0, len = lockStatusInfo.length; i < len; i++) {
                int status = lockStatusInfo[i] & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
                lockMap.put(i + 1, status == 0 ? "关" : "开");
            }
            return lockMap;
        }
        return null;
    }

    /**
     * 根据锁序号获取锁状态
     *
     * @param lockStatusInfo
     * @param lockIndex
     * @return
     */
    public static String getLockStatusByLockIndex(byte[] lockStatusInfo, int lockIndex) {
        if (lockStatusInfo == null || lockIndex > lockStatusInfo.length) {
            return "-";
        }
        int status = lockStatusInfo[lockIndex - 1] & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
        return status == 0 ? "关" : "开";
    }

    /**
     * 获取锁开关状态
     *
     * @param lockStatusInfo
     * @return
     */
    public static String getLockStatus(byte lockStatusInfo) {
        int status = lockStatusInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
        return status == 0 ? "关" : "开";
    }

    public static int getLockStatusValue(byte lockStatusInfo) {
        int status = lockStatusInfo & AlarmBitMarkConst.LOCK_ALARM_BIT_8_ON_OFF;
        return status == 0 ? 1 : 2;
    }

    /**
     * 构建报警标识
     *
     * @param vehicleId  车辆ID
     * @param deviceType 设备类型
     * @param deviceId   设备Id
     * @param alarmType  报警类型
     * @param lockId     锁记录ID
     * @return 14字节16进制字符串
     */
    public static String buildAlarmTag(int vehicleId, byte deviceType, int deviceId, byte alarmType, int lockId) {
        return new StringBuilder()
                .append(NumberHexUtil.intToHex(vehicleId))
                .append(NumberHexUtil.byteToHex(deviceType))
                .append(NumberHexUtil.intToHex(deviceId))
                .append(NumberHexUtil.byteToHex(alarmType))
                .append(NumberHexUtil.intToHex(lockId))
                .toString();
    }

    /**
     * 构建报警标识
     *
     * @param alarmDevice {@link AlarmDevice}
     * @return 14字节16进制字符串
     */
    public static String buildAlarmTag(AlarmDevice alarmDevice) {
        return new StringBuilder()
                .append(NumberHexUtil.intToHex((int) alarmDevice.getVehicleId()))
                .append(NumberHexUtil.byteToHex((byte) alarmDevice.getDeviceType()))
                .append(NumberHexUtil.intToHex(alarmDevice.getDeviceId()))
                .append(NumberHexUtil.byteToHex((byte) alarmDevice.getAlarmType()))
                .append(NumberHexUtil.intToHex(alarmDevice.getLockId()))
                .toString();
    }

    /**
     * 构建报警标识
     *
     * @param alarmInfo {@link AlarmInfo}
     * @return 14字节16进制字符串
     */
    public static String buildAlarmTag(AlarmInfo alarmInfo) {
        return new StringBuilder()
                .append(NumberHexUtil.intToHex(alarmInfo.getVehicleId()))
                .append(NumberHexUtil.byteToHex((byte) alarmInfo.getDeviceType()))
                .append(NumberHexUtil.intToHex(alarmInfo.getDeviceId()))
                .append(NumberHexUtil.byteToHex((byte) alarmInfo.getAlarmType()))
                .append(NumberHexUtil.intToHex(alarmInfo.getAlarmLock().getLockId()))
                .toString();
    }

}
