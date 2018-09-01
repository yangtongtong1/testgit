package PSM.Tool;

public class Validater {
	/**
	 * @param args
	 */
	public static boolean validate(String searchSQL) {
		if(searchSQL.equals("")||searchSQL==null)
			return true;
		
		searchSQL = searchSQL.toLowerCase();
		String forbidden[] = {"select","update","delete","exec","insert","truncate","declare","varchar","master"}; 
		for(int i=0;i<9;i++)
		{
			if(searchSQL.contains(forbidden[i]))
				return false;
		}
		return true;
	}
	public static boolean validate2(String searchSQL) {
		if(searchSQL.equals("")||searchSQL==null)
			return true;
		
		searchSQL = searchSQL.toLowerCase();
		String forbidden[] = {"update","delete","exec","insert","truncate","declare","varchar","master"}; 
		for(int i=0;i<8;i++)
		{
			if(searchSQL.contains(forbidden[i]))
				return false;
		}
		return true;
	}
	
}