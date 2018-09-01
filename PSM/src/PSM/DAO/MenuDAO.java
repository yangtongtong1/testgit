package PSM.DAO;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import hibernate.Menus;

public class MenuDAO extends HibernateDaoSupport{
	
	/**
	 *查询MenuList
	 * @return MenuList
	 */
	public List<Menus> getMenuList(String role){
		String hql = "from Menus menu where menu.role like '%" + role + "%' order by OrderID asc" ;
		List<Menus> list = this.getHibernateTemplate().find(hql);
		return list;
	}
	
	/**
	 * 查询某个二级菜单Menu的ID�?
	 * @param name:Menu.pName
	 * @return
	 */
	public int getChildId(String name, String role){
		int i;
		Menus menu = null;
		String hql = "from Menus menu where menu.name = '" + name + "' and menu.role like '%" + role + "%'";	
		List<Menus> list = this.getHibernateTemplate().find(hql);
		for(i=0; i<list.size(); i++){	//
			if(list.get(i).getPid()!=0 && list.get(i).getPpid()==0){
				menu = list.get(i);
				break;
			}
		}
		return menu.getId();
	}
	
	/**
	 * 查询二级菜单�?
	 * 
	 */
	public String getChildName(int ID)
	{
		int i;
		String name = null;
		String hql = "from Menus menu where menu.id = '" + ID + "'";	
		List<Menus> list = this.getHibernateTemplate().find(hql);
		name = list.get(0).getName();
		return name;
	}

}
