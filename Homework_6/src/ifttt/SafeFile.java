package ifttt;

import java.io.File;
import java.io.IOException;

public class SafeFile {
	private File file;
	private long lastModifiedTime;
	private long lastLength;
	private final static Object lock = new Object();
	public SafeFile(File file) {
		synchronized (lock) {
			this.file = file;
			// location= this.file.getParent();
			lastModifiedTime = this.file.lastModified();
			lastLength = this.file.length();
		}
	}

	public SafeFile(String file) {
		synchronized (lock) {
			this.file = new File(file);
			// location= this.file.getParent();
			lastModifiedTime = this.file.lastModified();
			lastLength = this.file.length();
		}
	}

	public SafeFile() {

	}

	public Boolean delete() {
		synchronized (lock) {
			return file.delete();
		}
	}

	public Boolean createNewFile() {
		try {
			synchronized (lock) {
				return file.createNewFile();
			}
		} catch (IOException e) {
			return false;
		}

	}

	public Boolean mkdirs() {
		synchronized (lock) {
			return file.mkdirs();
		}
	}

	public Boolean renameTo(File newFile) {
		synchronized (lock) {
			return file.renameTo(newFile);
		}
	}

	public long lastModified() {
		synchronized (lock) {
			return file.lastModified();
		}
	}

	public Boolean isDirectory() {
		synchronized (lock) {
			return file.isDirectory();
		}
	}

	public Boolean isFile() {
		synchronized (lock) {
			return file.isFile();
		}
	}

	public long getOriginTime() {
		synchronized (lock) {
			return lastModifiedTime;
		}
	}

	public long length() {
		synchronized (lock) {
			return file.length();
		}
	}

	public long getOriginLength() {
		synchronized (lock) {
			return lastLength;
		}
	}

	public String getName() {
		synchronized (lock) {
			return file.getName();
		}
	}

	public String getParent() {
		synchronized (lock) {
			return file.getParent();
		}
	}

	public String getAbsolutePath() {
		synchronized (lock) {
			return file.getAbsolutePath();
		}
	}

	public Boolean exists() {
		synchronized (lock) {
			return file.exists();
		}
	}

	public File getParentFile() {
		synchronized (lock) {
			return file.getParentFile();
		}
	}

	public File getFile() {
		synchronized (lock) {
			return file;
		}
	}

	public Boolean setLastModified(long time) {
		synchronized (lock) {
			return file.setLastModified(time);
		}
	}

	public Boolean renameTo(SafeFile file2) {
		synchronized (lock) {
			if (file.renameTo(file2.getFile())) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String toString() {
		synchronized (lock) {
			return file.getName() + " " + file.getAbsolutePath() + " " + lastModifiedTime + " " + lastLength + "bytes ";
		}
	}
}
