package PSM.Tool;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import PSM.DAO.FileSystemDAO;
import hibernate.Commonlaw;

public class InsertTask extends RecursiveAction {
	
	private FileSystemDAO fileSystemDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Commonlaw> list;
	
	private int first;
	
	private int last;
	
	

	public InsertTask(FileSystemDAO fileSystemDAO, List<Commonlaw> list, int first, int last) {
		super();
		this.fileSystemDAO = fileSystemDAO;
		this.list = list;
		this.first = first;
		this.last = last;
	}



	@Override
	protected void compute() {
		
		if((last - first) < 10) {
			InsertToDB(list,first,last);
		}else {
			int mid = (last + first)/2;
			InsertTask task1 = new InsertTask(fileSystemDAO,list, first, mid);
			InsertTask task2 = new InsertTask(fileSystemDAO,list, mid+1, last);
			invokeAll(task1,task2);
		}
		
	}



	private void InsertToDB(List<Commonlaw> list, int first, int last) {
		for(int i=first;i<=last;i++) {
			fileSystemDAO.insertCommonlaw(list.get(i));
		}
	}

}
