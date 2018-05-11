package com.tipray.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.User;
import com.tipray.core.exception.ServiceException;

/**
 * UserService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface UserService {
	/**
	 * 新增操作员
	 * 
	 * @param user
	 * @throws ServiceException
	 * @throws NoSuchAlgorithmException
	 */
    User addUser(User user, Long roleId) throws ServiceException, NoSuchAlgorithmException;

	/**
	 * 修改操作员信息
	 * 
	 * @param user
	 */
    User updateUser(User user, Long roleId) throws ServiceException;

	/**
	 * 修改密码
	 * 
	 * @param user
	 * @throws ServiceException
	 * @throws NoSuchAlgorithmException
	 */
    void updatePassword(User user) throws ServiceException, NoSuchAlgorithmException;

	/**
	 * 根据Id删除操作员
	 * 
	 * @param user
	 */
    void deleteUserById(Long id) throws ServiceException;

	/**
	 * 根据Id获取操作员信息
	 * 
	 * @param id
	 * @return
	 */
    User getUserById(Long id);

	/**
	 * 根据账号获取操作员信息
	 * 
	 * @param account
	 * @return
	 */
    User getUserByAccount(String account);

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
	 * 查询所有的操作员信息列表
	 * 
	 * @param
	 * @return
	 */
    List<User> findAllUsers();

	/**
	 * 获取操作员数目
	 * 
	 * @return
	 */
    long countUser(User user);

	/**
	 * 分页查询操作员信息
	 * 
	 * @param user
	 * @param page
	 * @return
	 */
    List<User> findByPage(User user, Page page);

	/**
	 * 分页查询操作员信息
	 * 
	 * @param user
	 * @param page
	 * @return
	 */
    GridPage<User> findUsersForPage(User user, Page page);
}
