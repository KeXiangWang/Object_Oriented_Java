package ifttt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class TestThread implements Runnable {
	private Shutter shutter;
	private Detail detail;
	private Summary summary;

	public void run() {
		try {
			if (testcase()) {
			}
//			 endAll();
		} catch (Exception e) {
		}
	}

	public boolean testcase() {

		try {
			Thread.sleep(1000);
			/* Write your test code here please */
//			 setAllDirectory();// before execute example()
//			example();

			//
			return true;
		} catch (Throwable e) {
			return false;
		}

	}

	public TestThread(Shutter shutter, Summary summary, Detail detail) {
		this.shutter = shutter;
		this.summary = summary;
		this.detail = detail;
	}

	public Boolean setAllDirectory() {
		try {
			mkdir("D:\\test");
			int i;
			for (i = 0; i < 3; i++) {
				mkdir("D:\\test\\outDir" + i);
			}
			delete("D:\\test\\outDir0");
			for (i = 0; i < 2; i++) {
				mkdir("D:\\test\\outDir0", "secondDir" + i);
			}
			for (i = 0; i < 4; i++) {
				touch("D:\\test\\outDir0", "testFile" + (char) (i + 'a'));
			}
			for (i = 10; i < 14; i++) {
				touch("D:\\test\\outDir0\\secondDir0", "testFile" + i);
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public void example() {
		renameTo("D:\\test\\outDir0\\testFileb", "D:\\test\\outDir0\\renamedfileb");
		renameTo("D:\\test\\outDir0\\secondDir0\\testFile10", "D:\\test\\outDir0\\secondDir0\\generatedFile");
		moveTo("D:\\test\\outDir0\\secondDir0", "D:\\test\\outDir0", "testFile11");
		modify("D:\\test\\outDir0\\secondDir0\\testFile12");
		sizeChange("D:\\test\\outDir0\\secondDir0\\testFile13");
		modify("D:\\test\\outDir0\\testFilec");
		sizeChange("D:\\test\\outDir0\\testFiled");
	}

	public Boolean renameTo(String originName, String newName) {
		SafeFile file1 = new SafeFile(originName);
		if (!file1.isFile()) {
			System.out.println(originName + " does not exist!");
			return false;
		}
		SafeFile file2 = new SafeFile(newName);
		return file1.renameTo(file2);
	}

	public void endAll() {
		try {
			Thread.sleep(1000);
			shutter.setState(1);
			synchronized (summary) {
				summary.notify();
			}
			synchronized (detail) {
				detail.notify();
			}

		} catch (InterruptedException e) {
		}
	}

	public Boolean moveTo(String originDirectory, String newDirectory, String file) {
		try {
			Thread.sleep(1000);
			SafeFile file1 = new SafeFile(originDirectory + File.separatorChar + file);
			if (!file1.exists()) {
				System.out.println(file1.getName() + " does not exist!");
				return false;
			}
			SafeFile file2 = new SafeFile(newDirectory);
			if (!file2.isDirectory()) {
				System.out.println(newDirectory + " is not a directory");
				return false;
			}
			SafeFile file3 = new SafeFile(newDirectory + File.separatorChar + file);
			if (file3.exists()) {
				System.out.println(file3.getName() + " there has been it");
				return false;
			}
			return file1.renameTo(file3.getFile());
		} catch (InterruptedException e) {
			return false;
		}

	}

	public Boolean mkdir(String directory) {
		try {
			Thread.sleep(1000);
			SafeFile newDir = new SafeFile(directory);
			if (newDir.exists()) {
				return true;
			}
			newDir.mkdirs();
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	public Boolean mkdir(String fatherDirectory, String sonDirectory) {
		try {
			Thread.sleep(1000);
			SafeFile newDir = new SafeFile(fatherDirectory + File.separator + sonDirectory);
			if (newDir.exists()) {
				return true;
			}
			return newDir.mkdirs();
		} catch (InterruptedException e) {
			return false;
		}
	}

	public Boolean touch(String file) {
		try {
			Thread.sleep(1000);
			System.out.println(file);
			SafeFile newFile = new SafeFile(file);
			if (newFile.exists()) {
				return true;
			}
			newFile.createNewFile();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public Boolean delete(String file) {
		try {
			Thread.sleep(1000);
			SafeFile newFile = new SafeFile(file);
			if (!newFile.exists()) {
				return true;
			}
			newFile.delete();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Boolean touch(String directory, String file) {
		try {
			Thread.sleep(1000);
			System.out.println(directory + File.separator + file);
			SafeFile newFile = new SafeFile(directory + File.separator + file);

			if (newFile.exists()) {
				return true;
			}
			newFile.createNewFile();

			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public Boolean delete(String directory, String file) {
		try {
			Thread.sleep(1000);
			SafeFile newFile = new SafeFile(directory + File.separator + file);
			if (!newFile.exists()) {
				return true;
			}
			newFile.delete();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public Boolean sizeChange(String file) {
		BufferedWriter out;
		try {
			Thread.sleep(1000);
			SafeFile file2 = new SafeFile(file);
			out = new BufferedWriter(new FileWriter(file, true));
			out.write(System.currentTimeMillis() + "\r\n");
			out.close();
			file2.setLastModified(System.currentTimeMillis());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Boolean modify(String originFile) {
		try {
			Thread.sleep(1000);
			SafeFile file = new SafeFile(originFile);
			file.setLastModified(System.currentTimeMillis());
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}