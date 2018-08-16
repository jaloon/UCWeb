package com.tipray.dao;

import com.tipray.bean.baseinfo.TransportCard;
import com.tipray.bean.baseinfo.User;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

import java.util.List;

/**
 * UserDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface UserDao extends BaseDao<User> {
    /**
     * 统计账号数目
     * @param account
     * @return
     */
    Integer countByAccount(String account);

    /**
     * 根据账号更新用户
     * @param user
     */
    void updateByAccount(User user);

    /**
     * 根据账号删除用户
     * @param account
     */
    void deleteByAccount(String account);

	/**
	 * 修改密码
	 * 
	 * @param user
	 */
    void updatePassword(User user);

	/**
	 * 根据账号获取操作员信息
	 * 
	 * @param account
	 * @return
	 */
    User getByAccount(String account);

	/**
	 * 根据身份证号获取操作员信息
	 * 
	 * @param identityCard
	 * @return
	 */
    User getByIDCard(String identityCard);

	/**
	 * 根据账号查询操作员信息列表
	 * 
	 * @param account
	 * @return
	 */
    List<User> findByAccount(String account);

	/**
	 * 根据姓名查询操作员信息列表
	 * 
	 * @param name
	 * @return
	 */
    List<User> findByName(String name);

	/**
	 * 重置用户角色
	 * 
	 * @param roleId
	 */
    void resetUserRole(Long roleId);
	
	/**
	 * 移除APP角色
	 * 
	 * @param appRoleId
	 */
    void removeAppRole(Long appRoleId);

}
