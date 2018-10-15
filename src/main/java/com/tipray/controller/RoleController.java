package com.tipray.controller;

import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.Permission;
import com.tipray.bean.baseinfo.Role;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.constant.LogTypeConst;
import com.tipray.constant.PageActionMode;
import com.tipray.core.ThreadVariable;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.PermissionException;
import com.tipray.service.InfoManageLogService;
import com.tipray.service.PermissionService;
import com.tipray.service.RoleService;
import com.tipray.util.OperateLogUtil;
import com.tipray.util.PermissionUtil;
import com.tipray.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色管理控制器
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Controller
@RequestMapping("/manage/role")
// @Scope("prototype")
public class RoleController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Resource
    private InfoManageLogService infoManageLogService;

    @PermissionAnno("roleModule")
    @RequestMapping(value = "dispatch.do")
    public String dispatch(String mode, Long id, ModelMap modelMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch role edit page, mode={}, id={}", mode, id);
        }
        modelMap.put("mode", mode);
        Role role = new Role();
        if (id != null && id > 0) {
            if (PageActionMode.EDIT.equals(mode) && id < 21) {
                throw new PermissionException("系统内置角色不允许修改！");
            }
            role = roleService.getRoleById(id);
        }
        modelMap.put("role", role);
        return "normal/role/roleEdit.jsp";
    }

    @PermissionAnno("editRole")
    @RequestMapping(value = "add.do")
    @ResponseBody
    public Message addRole(@ModelAttribute Role role) {
        if (logger.isDebugEnabled()) {
            logger.debug("add role, role={}", role);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_ROLE
                | LogTypeConst.TYPE_INSERT | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("添加角色：" + role.getName()).append('。');
        try {
            roleService.addRole(role);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("添加角色异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editRole")
    @RequestMapping(value = "update.do")
    @ResponseBody
    public Message updateRole(@ModelAttribute Role role) {
        if (logger.isDebugEnabled()) {
            logger.debug("update role, role={}", role);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_ROLE
                | LogTypeConst.TYPE_UPDATE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("修改角色：").append(role.getName()).append('。');
        try {
            if (role.getId() < 21) {
                logger.warn("{}是系统内置角色，不允许修改！", role.getName());
                type++;
                description.append("失败，系统内置角色不允许修改！");
                return Message.error("系统内置角色不允许修改！");
            }
            roleService.updateRole(role);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("修改角色异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @PermissionAnno("editRole")
    @RequestMapping(value = "delete.do")
    @ResponseBody
    public Message deleteRoles(Long id, Integer app) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete role, id={}", id);
        }
        InfoManageLog infoManageLog = new InfoManageLog(ThreadVariable.getUser());
        Integer type = LogTypeConst.CLASS_BASEINFO_MANAGE | LogTypeConst.ENTITY_ROLE
                | LogTypeConst.TYPE_DELETE | LogTypeConst.RESULT_DONE;
        StringBuffer description = new StringBuffer("删除角色：");
        try {
            if (id < 21) {
                logger.warn("角色【{}】是系统内置角色，不允许删除！", id);
                type++;
                description.append("失败，系统内置角色不允许删除！");
                return Message.error("系统内置角色不允许删除！");
            }
            roleService.deleteRolesById(id, app);
            description.append("成功！");
            return Message.success();
        } catch (Exception e) {
            type++;
            description.append("失败！");
            logger.error("删除角色异常！", e);
            return Message.error(e);
        } finally {
            OperateLogUtil.addInfoManageLog(infoManageLog, type, description.toString(), infoManageLogService, logger);
        }
    }

    @RequestMapping("isExist.do")
    @ResponseBody
    public Boolean isRoleExist(@ModelAttribute Role role) {
        if (logger.isDebugEnabled()) {
            logger.debug("role exist, role={}", role);
        }
        Role bean = roleService.getRoleByName(role.getName());
        return bean != null && role.getId() != bean.getId();
    }

    @PermissionAnno("viewRole")
    @RequestMapping(value = "ajaxFindForPage.do")
    @ResponseBody
    public GridPage<Role> ajaxFindRolesForPage(@ModelAttribute Role role, @ModelAttribute Page page) {
        if (logger.isDebugEnabled()) {
            logger.debug("role list page, role={}, page={}", role, page);
        }
        return roleService.findRolesForPage(role, page);
    }

    @RequestMapping(value = "findAllRoles.do")
    @ResponseBody
    public List<Role> findAllRoles() {
        List<Role> roles = roleService.findAllRoles();
        for (Role role : roles) {
            if (role.getId().equals(ThreadVariable.getRole().getId())) {
                roles.remove(role);
                break;
            }
        }
        return roles;
    }

    @RequestMapping(value = "findPermissions.do")
    @ResponseBody
    public List<Permission> findPermissions(String mode, Long roleId) {
        if (logger.isDebugEnabled()) {
            logger.debug("find permissions, mode={}, roleId={}", mode, roleId);
        }
        if (PageActionMode.ADD.equals(mode)) {
            List<Permission> permissions = permissionService.findOperatePermissions();
            permissions.parallelStream().forEach(permission -> permission.setOpen(permission.getParentId() == 0));
            return permissions;
        }
        Role role = roleService.getRoleById(roleId);
        String permissionIds = role != null ? role.getPermissionIds() : null;
        if (StringUtil.isEmpty(permissionIds)) {
            return null;
        }
        List<Permission> list = permissionService.findPermissionsByIds(permissionIds);
        if (PageActionMode.VIEW.equals(mode)) {
            list.parallelStream().forEach(permission -> permission.setOpen(true));
            return list;
        }
        List<Permission> permissions = permissionService.findOperatePermissions();
        permissions = PermissionUtil.replace(permissions, list);
        return permissions;
    }

    @RequestMapping(value = "findAllPermissions.do")
    @ResponseBody
    public List<Permission> findAllPermissions() {
        return permissionService.findAllPermissions();
    }
}
