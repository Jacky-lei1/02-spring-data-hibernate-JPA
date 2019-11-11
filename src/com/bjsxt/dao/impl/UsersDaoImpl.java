package com.bjsxt.dao.impl;

import java.util.List;

import com.bjsxt.dao.UsersDao;
import org.springframework.stereotype.Repository;

import com.bjsxt.pojo.Users;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository
public class UsersDaoImpl  implements UsersDao {

	//会从工厂中取一个entityManager，并且注入到这个属性中
	@PersistenceContext(name = "entityManagerFactory")
	private EntityManager entityManager;

	/**
	 * 增加用户
	 * @param users
	 */
	@Override
	public void insertUsers(Users users) {
		this.entityManager.persist(users);
	}

	/**
	 * 更新用户
	 * @param users
	 */
	@Override
	public void updateUsers(Users users) {
		this.entityManager.merge(users);
	}

	/**
	 * 删除用户
	 * @param users
	 */
	@Override
	public void deleteUsers(Users users) {
		//先根据ID来查找，将查询到的数据映射到实体类中再删除
		Users u = this.selectUsersById(users.getUserid());
		this.entityManager.remove(u);
	}

	/**
	 * 根据ID查询用户
	 * @param userid
	 * @return
	 */
	@Override
	public Users selectUsersById(Integer userid) {

		return this.entityManager.find(Users.class,userid);
	}


	/**
	 * HQL测试
	 * @param username
	 * @return
	 */
	@Override
	public List<Users> selectUserByName(String username) {

		return this.entityManager.createQuery(" from Users where username = :abc").setParameter("abc",username).getResultList();

	}

	/**
	 * SQL测试
	 * @param username
	 * @return
	 */

	@Override
	public List<Users> selectUserByNameUseSQL(String username) {

		//第一个参数是需要执行的语句，第二个参数是当前SQL语句所要返回的结果集，需要和哪个对象作映射
		//参数绑定可以用:abc,也可以用问号。
		//在Hibernate JPA中，如果通过问号的方式来绑定参数，那么它的查数是从1开始，而Hibernate中是从0开始
		return	this.entityManager.createNativeQuery("select * from t_users where username = ?",Users.class).setParameter(1,username).getResultList();


	}

	/**
	 * QBC测试
	 * @param username
	 * @return
	 */

	@Override
	public List<Users> selectUserByNameUseCriteria(String username) {
		//创建一个CriteriaBuilder对象，创建一个CriteriaQuery,创建查询条件
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		//CriteriaQuery对象：执行查询的Criteria对象
		//select * from t_users 下面这句代码相当于执行了这句
		CriteriaQuery<Users> query = builder.createQuery(Users.class);
		//获取要查询的实体类的对象
		Root<Users> root = query.from(Users.class);
		//封装查询条件
		Predicate cate = builder.equal(root.get("username"), username);
		//select * from t_users where username = "赵六"
		query.where(cate);
		//执行查询
		TypedQuery<Users> typedQuery = this.entityManager.createQuery(query);
		return typedQuery.getResultList();

	}

}
