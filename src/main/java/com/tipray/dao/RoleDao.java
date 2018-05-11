package com.tipray.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.baseinfo.Role;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * RoleDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface RoleDao extends BaseDao<Role> {
	/**
	 * 根据角色名称获取角色
	 * 
	 * @param name
	 * @return
	 */
    Role getByName(String name);

	/**
	 * 根据Id和权限名查找角色
	 * 
	 * @param id
	 * @param ename
	 * @return
	 */
    Role getByIdAndPermission(@Param("id") Long id, @Param("ename") String permissionEname);

	/**
	 * 根据Id集合获取角色
	 * 
	 * @param ids
	 * @return
	 */
    List<Role> findByIds(String ids);

}
