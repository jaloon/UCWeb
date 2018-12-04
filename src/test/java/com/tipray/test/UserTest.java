package com.tipray.test;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.Session;
import com.tipray.bean.baseinfo.User;
import com.tipray.core.ThreadVariable;
import com.tipray.core.exception.LoginException;
import com.tipray.core.exception.PermissionException;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.RoleService;
import com.tipray.service.SessionService;
import com.tipray.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 操作员管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class UserTest {
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	@Resource
	private SessionService sessionService;

	@Test
	public void addUser() {
		User user = new User();
		user.setAccount("admin");
		user.setPassword("123");
		user.setName("Admin");
		user.setPhone("13612345678");
		user.setIdentityCard("640402197611251235");

		User user4 = new User();
		user4.setAccount("aaa");
		user4.setPassword("123");
		user4.setName("张三");
		user4.setPhone("13612345678");
		user4.setIdentityCard("640402197611251235");

		User user1 = new User();
		user1.setAccount("bbb");
		user1.setPassword("456");
		user1.setName("李四");
		user1.setPhone("13612345679");
		user1.setIdentityCard("640402197811251235");

		User user2 = new User();
		user2.setAccount("ccc");
		user2.setPassword("789");
		user2.setName("王五");
		user2.setPhone("15912345678");
		user2.setIdentityCard("640326197611251235");

		User user3 = new User();
		user3.setRole(roleService.getRoleById(2L));
		user3.setAccount("ddd");
		user3.setPassword("682");
		user3.setName("赵六");
		user3.setPhone("13612965678");
		user3.setIdentityCard("642122197611251235");

		try {
			/*
			 * userService.addUser(user,1L); userService.addUser(user1,2L);
			 * userService.addUser(user2,2L); userService.addUser(user3,2L);
			 */
			userService.addUser(user4);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void updateUser() {
		User user = new User();
		user.setId(1L);
		user.setAccount("aaaa");
		user.setPassword("1234");
		user.setName("李四");
		user.setPhone("13912345678");
		user.setIdentityCard("640402197611251236");
		try {
			userService.updateUser(user);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void updatePassword() {
		User user = new User();
		user.setId(1L);
		user.setAccount("aaaa");
		user.setPassword("1234");
		user.setName("张三");
		user.setPhone("13612345678");
		user.setIdentityCard("640402197611251235");
		try {
			userService.updatePassword(user);
		} catch (NoSuchAlgorithmException | ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findAllUsers() {
		List<User> ulist = userService.findAllUsers();
		System.out.println();
		for (User user : ulist) {
			System.out.println(user.getId() + "：" + user);
		}
		System.out.println();
	}

	@Test
	public void deleteUser() {
		try {
			userService.deleteUserById(2L);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getById() {
		User user = userService.getUserById(3L);
		System.out.println(user + "\n" + user.getRole().getId());
	}

	@Test
	public void getByAccount() throws LoginException, PermissionException {
		User user = userService.getUserByAccount("admin");
		System.out.println(user + "\n" + user.getRole().getId());
		user.setPassword("123");
		Session session = new Session();
		session.setUuid("BBD7B5BADBECF4AAA044014CDFD43819");
		session.setIp("127.0.0.1");
		session = sessionService.login(user.getAccount(), user.getPassword(), session,0);
		System.out.println(session);
		session = ThreadVariable.getSession();
		System.out.println(session);
	}

	@Test
	public void getByIDCard() {
		System.out.println(userService.getByIDCard("640402197611251236"));
	}

	@Test
	public void findByAccount() {
		System.out.println(userService.findByAccount("a"));
	}

	@Test
	public void findByName() {
		System.out.println(userService.findByName("三"));
	}

	@Test
	public void countUser() {
		User user = new User();
		user.setAccount("aaa");
		System.out.println(userService.countUser(user));
	}

	@Test
	public void findByPage() {
		Page page = new Page();
		page.setPageId(1);
		page.setRows(10);
		page.setStartRow(0);
		User user = new User();
		List<User> ulist = userService.findByPage(user, page);
		System.out.println();
		for (User u : ulist) {
			System.out.println(u.getId() + "：" + u);
		}
		System.out.println();
		GridPage<User> gp = new GridPage<User>();
		gp = userService.findUsersForPage(user, page);
		System.out.println(gp);
	}
}
