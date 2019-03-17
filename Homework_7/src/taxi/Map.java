package taxi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Map {
	private String possibleNumber = "0123";
	protected int[][] intMap;

	public Map(File file) {
		intMap = new int[TaxiSys.SIZE][TaxiSys.SIZE];
		if (mapExsits(file)) {
			parseMap(file);
		}
	}

	private Boolean mapExsits(File file) {
		if (file.exists()) {
			return true;
		} else {
			System.out.println("The map file do not exsit so that the program ends!");
			System.exit(1);
			return false;
		}
	}

	private void parseMap(File file) {
		try {
			InputStream inputStream = new FileInputStream(file);
			Scanner in = new Scanner(inputStream);
			int line = 0;
			while (in.hasNextLine()) {
				String str = in.nextLine();
				str = str.replaceAll(" ", "").replaceAll("\t", "");
				
				if (str.equals(""))
					continue;
				else {
					parseLine(str, line);
					line++;
					if (line == TaxiSys.SIZE) {
						break;
					}
				}
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Wrong map file input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	private void parseLine(String str, int line) throws Exception {
		if (str.length() != TaxiSys.SIZE) {
			throw new Exception("Too many columns");
		}
		for (int i = 0; i < TaxiSys.SIZE; i++) {
			if (possibleNumber.indexOf(str.charAt(i)) == -1) {
				throw new Exception("Illegal number");
			}
			intMap[line][i] = possibleNumber.indexOf(str.charAt(i));
		}
	}
}
