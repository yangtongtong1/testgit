package hibernate;
// Generated 2017-9-4 16:20:13 by Hibernate Tools 5.1.4.Final

/**
 * PrescribedActionStatic generated by hbm2java
 */
public class PrescribedActionStatic implements java.io.Serializable {

	private int id;
	private String prescribedAction;
	private String relatedMenu;
	private String promptRole;
	private String promptCycle;

	public PrescribedActionStatic() {
	}

	public PrescribedActionStatic(int id) {
		this.id = id;
	}

	public PrescribedActionStatic(int id, String prescribedAction, String relatedMenu, String promptRole,
			String promptCycle) {
		this.id = id;
		this.prescribedAction = prescribedAction;
		this.relatedMenu = relatedMenu;
		this.promptRole = promptRole;
		this.promptCycle = promptCycle;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrescribedAction() {
		return this.prescribedAction;
	}

	public void setPrescribedAction(String prescribedAction) {
		this.prescribedAction = prescribedAction;
	}

	public String getRelatedMenu() {
		return this.relatedMenu;
	}

	public void setRelatedMenu(String relatedMenu) {
		this.relatedMenu = relatedMenu;
	}

	public String getPromptRole() {
		return this.promptRole;
	}

	public void setPromptRole(String promptRole) {
		this.promptRole = promptRole;
	}

	public String getPromptCycle() {
		return this.promptCycle;
	}

	public void setPromptCycle(String promptCycle) {
		this.promptCycle = promptCycle;
	}

}
