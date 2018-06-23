package com.tipray.cache;

import com.tipray.bean.upgrade.TerminalUpgradeFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车台升级缓存
 *
 * @author chenlong
 * @version 1.0 2018-06-22
 */
public final class TerminalUpgradeCache {
    /**
     * FTP路径缓存
     */
    private static final Map<Long, String> FTP_PATH_CACHE = new HashMap<>();
    /**
     * 升级类型缓存
     */
    private static final Map<Long, Byte> UPGRADE_TYPE_CACHE = new HashMap<>();
    /**
     * 车台ID缓存
     */
    private static final Map<Long, List<Integer>> TERMINAL_IDS_CACHE = new HashMap<>();
    /**
     * 升级版本缓存
     */
    private static final Map<Long, Integer> UPGRADE_VERSION_CACHE = new HashMap<>();
    /**
     * 升级文件缓存
     */
    private static final Map<Long, List<TerminalUpgradeFile>> UPGRADE_FILES_CACHE = new HashMap<>();

    /**
     * 添加车台升级缓存
     *
     * @param index          {@link Long} 缓存索引
     * @param ftpPath        {@link String} ftp路径
     * @param upgradeType    {@link Byte} 升级类型
     * @param terminalIdList {@link List} {@link Integer} 车台ID
     * @param ver            {@link Integer} 升级版本
     * @param files          {@link List} {@link TerminalUpgradeFile}  升级文件
     */
    public static synchronized void putCache(Long index,
                                             String ftpPath,
                                             Byte upgradeType,
                                             List<Integer> terminalIdList,
                                             Integer ver,
                                             List<TerminalUpgradeFile> files) {
        FTP_PATH_CACHE.put(index, ftpPath);
        UPGRADE_TYPE_CACHE.put(index, upgradeType);
        TERMINAL_IDS_CACHE.put(index, terminalIdList);
        UPGRADE_VERSION_CACHE.put(index, ver);
        UPGRADE_FILES_CACHE.put(index, files);
    }

    /**
     * 获取并移除ftp路径缓存
     *
     * @param index {@link Long} 缓存索引
     * @return {@link String} ftp路径
     */
    public static synchronized String getAndRemoveFtpPath(Long index) {
        return FTP_PATH_CACHE.remove(index);
    }

    /**
     * 获取并移除升级类型缓存
     *
     * @param index {@link Long} 缓存索引
     * @return {@link Byte} 升级类型
     */
    public static synchronized Byte getAndRemoveUpgradeType(Long index) {
        return UPGRADE_TYPE_CACHE.remove(index);
    }

    /**
     * 获取并移除车台ID缓存
     *
     * @param index {@link Long} 缓存索引
     * @return {@link List} {@link Integer} 车台ID
     */
    public static synchronized List<Integer> getAndRemoveTerminalIds(Long index) {
        return TERMINAL_IDS_CACHE.remove(index);
    }

    /**
     * 获取并移除升级版本缓存
     *
     * @param index {@link Long} 缓存索引
     * @return {@link Integer} 升级版本
     */
    public static synchronized Integer getAndRemoveUpgradeVersion(Long index) {
        return UPGRADE_VERSION_CACHE.remove(index);
    }

    /**
     * 获取并移除升级文件缓存
     *
     * @param index {@link Long} 缓存索引
     * @return {@link List} {@link TerminalUpgradeFile}  升级文件
     */
    public static synchronized List<TerminalUpgradeFile> getAndRemoveUpgradeFiles(Long index) {
        return UPGRADE_FILES_CACHE.remove(index);
    }

    private TerminalUpgradeCache() {
    }
}
