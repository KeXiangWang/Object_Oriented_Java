package ifttt;

import java.io.File;
import java.util.ArrayList;

public class Monitor implements Runnable {
	private Request request;
	private Summary summary;
	private Detail detail;
	private Shutter shutter;
	private Boolean monitorAFile;
	private SafeFile monitoringFile;
	private Trigger trigger;
	private Boolean recordSummaryCommmand;
	private Boolean recordDetailCommand;
	private Boolean recoverCommand;
	private String location;
	private ArrayList<SafeFile> lastFileList;
	private ArrayList<SafeFile> fileList;
	private SafeFile originFile;
	private SafeFile newFile;

	public Monitor() {

	}

	public Monitor(Request request, Summary summary, Detail detail, Shutter shutter) {
		fileList = new ArrayList<SafeFile>();
		this.request = request;
		this.summary = summary;
		this.detail = detail;
		this.shutter = shutter;
		recoverCommand = false;
		recordDetailCommand = false;
		recordSummaryCommmand = false;
		monitorAFile = false;
		monitoringFile = null;
	}

	public int checkRequest(Request request) {
		if (this.request.getPath().equals(request.getPath()) && this.request.geTrigger() == request.geTrigger()) {
			if (request.geTask() == Task.RECORD_DETAIL) {
				if (!recordDetailCommand) {
					recordDetailCommand = true;
					return 1;
				} else {
					return 2;
				}
			} else if (request.geTask() == Task.RECORD_SUMMARY && !recordSummaryCommmand) {
				if (!recordSummaryCommmand) {
					recordSummaryCommmand = true;
					return 1;
				} else {
					return 2;
				}
			} else if (request.geTask() == Task.RECOVER) {

				if (!recoverCommand) {
					recoverCommand = true;
					return 1;
				} else {
					return 2;
				}
			}
		}
		return 0;
	}

	public Boolean parseRequest(Request request) {
		File fileName = new File(request.getPath());
		if (fileName.isDirectory() || fileName.isFile()) {
			if (fileName.isFile()) {
				location = fileName.getParent();
				monitorAFile = true;
				monitoringFile = new SafeFile(fileName);
			} else {
				location = fileName.getAbsolutePath();
			}
			getAllFiles(location, fileList);
		} else {
			return false;
		}
		trigger = request.geTrigger();
		if (request.geTask() == Task.RECORD_DETAIL) {
			recordDetailCommand = true;
		} else if (request.geTask() == Task.RECORD_SUMMARY) {
			recordSummaryCommmand = true;
		} else {
			recoverCommand = true;
		}
		lastFileList = fileList;
		return true;
	}

	public void getAllFiles(String fileDir, ArrayList<SafeFile> fileList) {
		File file = new File(fileDir);
		if (file.isFile()) {
			fileList.add(new SafeFile(file));
		} else {

			File[] files = file.listFiles();
			if (files == null) {
				return;
			}
			for (File f : files) {
				if (f.isFile()) {
					fileList.add(new SafeFile(f));
				} else {
					getAllFiles(f.getAbsolutePath(), fileList);
				}

			}
		}
	}

	public SafeFile getDifferent(ArrayList<SafeFile> list1, ArrayList<SafeFile> list2) {
		int miss = 1;
		for (SafeFile file1 : list1) {
			miss = 1;
			for (SafeFile file2 : list2) {
				if (file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
					miss = 0;
				}
			}
			if (miss == 1) {
				return file1;
			}
		}
		return null;
	}

	public String getPath() {
		if (monitorAFile) {
			return monitoringFile.getAbsolutePath();
		} else {
			return location;
		}
	}

	public Boolean checkTrigger() {
		if (trigger == Trigger.RENAMED) {
			if (monitorAFile) {
				if (monitoringFile.exists()) {
					return false;
				}
				File parentFile = monitoringFile.getFile().getParentFile();
				File[] files = parentFile.listFiles();
				for (File f : files) {
					if (f.isFile()) {
						if (!f.getName().equals(monitoringFile.getName())
								&& f.lastModified() == monitoringFile.getOriginTime()
								&& f.length() == monitoringFile.getOriginLength()) {
							originFile = monitoringFile;
							newFile = new SafeFile(f);
							monitoringFile = newFile;
							System.out.println(originFile.getName() + " Been Renamed as " + newFile.getName());
							return true;
						}

					} else {
					}
				}
			} else {

				SafeFile file1 = getDifferent(fileList, lastFileList);
				SafeFile file2 = getDifferent(lastFileList, fileList);
				if (file1 != null && file2 != null) {
					if (file1.getParent().equals(file2.getParent())) {
						if (file2.getOriginTime() == file1.lastModified()
								|| file2.getOriginLength() == file1.length()) {
							originFile = file2;
							newFile = file1;
							System.out.println(file2.getName() + " Been Renamed as " + file1.getName());
							return true;
						}
					}
				}
			}

		} else if (trigger == Trigger.MODIFIED) {
			if (monitorAFile) {
				if (!monitoringFile.exists()) {
					return false;
				} else {
					if (monitoringFile.lastModified() != monitoringFile.getOriginTime()
							&& monitoringFile.lastModified() != 0) {
						originFile = monitoringFile;
						newFile = new SafeFile(monitoringFile.getFile());
						monitoringFile = newFile;
						System.out.println(newFile.getName() + " at " + originFile.getOriginTime()
								+ " Been newly Modified at " + newFile.getOriginTime());
						return true;
					}
				}
			} else {
				for (SafeFile file1 : fileList) {
					for (SafeFile file2 : lastFileList) {
						if (file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
							if (file1.lastModified() != file2.getOriginTime() && file1.lastModified() != 0) {
								originFile = file2;
								newFile = file1;
								System.out.println(newFile.getName() + " at " + originFile.getOriginTime()
										+ " Been newly Modified at " + newFile.lastModified());
								return true;
							}
						}
					}
				}
			}
		} else if (trigger == Trigger.SIZE_CHANGED) {
			if (monitorAFile) {
				if (!monitoringFile.exists()) {
					return false;
				} else {
					if (monitoringFile.length() != monitoringFile.getOriginLength()
							&& monitoringFile.lastModified() != monitoringFile.getOriginTime()) {
						originFile = monitoringFile;
						newFile = new SafeFile(monitoringFile.getFile());
						monitoringFile = newFile;
						System.out.println(originFile.getName() + " size-changed from " + originFile.getOriginLength()
								+ " to " + newFile.getOriginLength());
						return true;
					}
				}
			} else {
				for (SafeFile file1 : fileList) {
					for (SafeFile file2 : lastFileList) {
						if (file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
							if (file1.length() != file2.getOriginLength()
									&& file1.lastModified() != file2.getOriginTime()) {
								originFile = file2;
								newFile = file1;
								System.out.println(originFile.getName() + " size-changed from "
										+ originFile.getOriginLength() + " to " + newFile.length());
								return true;
							}
						}
					}
				}
			}

		} else {
			if (monitorAFile) {
				if (monitoringFile.exists()) {
					return false;
				} else {
					for (SafeFile f : fileList) {
						if (f.getName().equals(monitoringFile.getName())
								&& f.lastModified() == monitoringFile.getOriginTime()
								&& f.length() == monitoringFile.getOriginLength()) {
							originFile = monitoringFile;
							newFile = f;
							monitoringFile = newFile;
							System.out.println(originFile.getName() + " Been Renamed as " + newFile.getName());
							return true;
						}

					}
				}
			} else {
				SafeFile file1 = getDifferent(fileList, lastFileList);
				SafeFile file2 = getDifferent(lastFileList, fileList);
				if (file1 != null && file2 != null) {
					if (file1.getName().equals(file2.getName())) {
						if (file2.getOriginTime() == file1.lastModified() && file2.getOriginLength() == file1.length()
								&& !file1.getAbsolutePath().equals(file2.getAbsolutePath())) {
							originFile = file2;
							newFile = file1;
							System.out.println(
									originFile.getAbsolutePath() + " Been Pathchanged as " + newFile.getAbsolutePath());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public Boolean executeTask() {
		if (recordSummaryCommmand) {
			synchronized (summary) {
				summary.setSummmary(trigger);
				summary.notify();
			}
			System.out.println("Summary Recorded " + trigger);
		}
		if (recordDetailCommand) {
			synchronized (detail) {
				detail.setDetail(trigger, originFile, newFile);
				detail.notify();
			}
			System.out.println("Detail Recorded  " + trigger);
		}
		if (recoverCommand) {
			newFile.renameTo(originFile);
			if (monitorAFile) {
				monitoringFile = originFile;
			}
			fileList = null;
			fileList = new ArrayList<SafeFile>();
			getAllFiles(location, fileList);
			System.out.println("Recovered        " + trigger);
		}
		return true;
	}

	public void run() {
		try {
			if (!parseRequest(request)) {
				System.out.println("no file");
				return;
			}
			Thread.sleep(500);
			while (true) {
				Thread.sleep(281);
				if (shutter.getState() == 1) {
					Thread.sleep(1000);
					break;
				}
				fileList = new ArrayList<SafeFile>();
				getAllFiles(location, fileList);
				if (checkTrigger()) {
					Thread.sleep(20);
					executeTask();
				}
				lastFileList = fileList;
				fileList = null;

			}
		} catch (Throwable e) {

		}
	}
}
