package ifttt;

public class Request {
	private String path;
	private String string_Trigger;
	private Trigger trigger;
	private String string_Task;
	private Task task;

	public Request() {

	}

	public Boolean setRequest(String path, String trigger, String task) {
		this.path = path;
		string_Task = task;
		string_Trigger = trigger;
		if (trigger.equals("renamed")) {
			this.trigger = Trigger.RENAMED;
		} else if (trigger.equals("modified")) {
			this.trigger = Trigger.MODIFIED;
		} else if (trigger.equals("path-changed")) {
			this.trigger = Trigger.PATH_CHANGED;
		} else if (trigger.equals("size-changed")) {
			this.trigger = Trigger.SIZE_CHANGED;
		} else {
			return false;
		}
		if (task.equals("record-summary")) {
			this.task = Task.RECORD_SUMMARY;
		} else if (task.equals("record-detail")) {
			this.task = Task.RECORD_DETAIL;
		} else if (task.equals("recover")) {
			this.task = Task.RECOVER;
		} else {
			return false;
		}
		return true;
	}

	public Trigger geTrigger() {
		return trigger;
	}

	public Task geTask() {
		return task;
	}

	public String getPath() {
		return path;
	}

	public String toString() {
		return "IF " + path + " " + string_Trigger + " THEN " + string_Task;
	}
}
