package test;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
class mapInfo{
	int[][] map=new int[80][80];
	public void readmap(String path){//读入地图信息
		//Requires:String类型的地图路径,System.in
		//Modifies:System.out,map[][]
		//Effects:从文件中读入地图信息，储存在map[][]中
		Scanner scan=null;
		File file=new File(path);
		if(file.exists()==false){
			System.out.println("地图文件不存在,程序退出");
			System.exit(1);
			return;
		}
		try {
			scan = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			
		}
		for(int i=0;i<80;i++){
			String[] strArray = null;
			try{
				strArray=scan.nextLine().split("");
			}catch(Exception e){
				System.out.println("地图文件信息有误，程序退出");
				System.exit(1);
			}
			for(int j=0;j<80;j++){
				try{
					this.map[i][j]=Integer.parseInt(strArray[j]);
				}catch(Exception e){
					System.out.println("地图文件信息有误，程序退出");
					System.exit(1);
				}
			}
		}
		scan.close();
	}
}
public class Main {
	public static TaxiGUI gui=new TaxiGUI();
	public static void PushRequest(int x1,int y1,int x2,int y2){
		gui.RequestTaxi(new Point(x1,y1), new Point(x2,y2));
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mapInfo mi=new mapInfo();
		mi.readmap("map.txt");//在这里设置地图文件路径
		gui.LoadMap(mi.map, 80);
//		Scanner scan=new Scanner(System.in);
//		while(true){
//			int x1=scan.nextInt();
//			int y1=scan.nextInt();
//			int x2=scan.nextInt();
//			int y2=scan.nextInt();
//			int status=scan.nextInt();
//			gui.SetRoadStatus(new Point(x1,y1), new Point(x2,y2), status);
//			System.out.println("\n改变");
//		}
		//gui.RequestTaxi(new Point(1,1), new Point(3,5));
		gv.stay(5000);
		gui.SetTaxiStatus(1, new Point(1,1), 1);
		gui.SetTaxiStatus(2, new Point(1,1), 1);
		gv.stay(100);
		gui.SetTaxiStatus(1, new Point(1,2), 1);
		gui.SetTaxiStatus(2, new Point(1,2), 1);
		gv.stay(100);
		gui.SetTaxiStatus(1, new Point(1,3), 1);
		gui.SetTaxiStatus(2, new Point(1,3), 1);
		gv.stay(100);
		gui.SetTaxiStatus(1, new Point(2,3), 1);
		gui.SetTaxiStatus(2, new Point(2,3), 1);
		gv.stay(100);
		gui.SetTaxiStatus(1, new Point(2,4), 1);
		gui.SetTaxiStatus(2, new Point(2,4), 1);
		gv.stay(100);
		gui.SetTaxiStatus(1, new Point(3,4), 1);
		gui.SetTaxiStatus(2, new Point(3,4), 1);
		gv.stay(100);
		gui.SetTaxiStatus(1, new Point(3,5), 1);
		gui.SetTaxiStatus(2, new Point(3,5), 1);
		
	}

}
