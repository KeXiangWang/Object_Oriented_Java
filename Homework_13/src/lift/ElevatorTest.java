package lift;

import static org.junit.Assert.*;

import java.io.FileInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.reflect.*;

public class ElevatorTest {
	private static int cnt = 0;

	@BeforeClass
	public static void setUpBeforeClass() {
		System.out.println("/**********ElevatorTest************/");
	}

	@Before
	public void before() {

		System.out.println("--------------test " + cnt + "---------------");
	}

	@After
	public void after() {
		System.out.println("------------------------------------");
	}

	@Test
	public void test0Main() { 
		try {
			cnt++;
			FileInputStream file1 = new FileInputStream("testMain.txt");
			System.setIn(file1); 
			Main.main(null);
			System.out.println("+++++++++++++++++++++++++++++++++");
			FileInputStream file2 = new FileInputStream("testNone.txt");
			System.setIn(file2); 
			Main.main(null); 
			System.out.println("+++++++++++++++++++++++++++++++++");
			FileInputStream file3 = new FileInputStream("testRUN.txt");
			System.setIn(file3);  
			Main.main(null);
			System.out.println("+++++++++++++++++++++++++++++++++");
			FileInputStream file4 = new FileInputStream("testTooMuch.txt");
			System.setIn(file4);  
			Main.main(null);
			System.out.println("+++++++++++++++++++++++++++++++++");
			Main main = new Main();
			assertEquals(true, main.repOK());
			throw new Exception(""); 
		} catch (Throwable e) {
			System.out.println("interesting");
		}
	}

	@Test
	public void test1Invalid() {
		cnt++;
		Queue queue = new Queue();
		SubScheduler scheduler = new SubScheduler();
		Elevator elevator = new Elevator();
		assertEquals(0, queue.getSize());
		scheduler.schedule(queue, elevator);
		queue.parse("(FR,,UP,0)");
		queue.parse("(FR,6,UP,0)"); // first test
		queue.parse("(FR,1,UP,5)");
		queue.parse("(FR,1,DOWN,0)");
		queue.parse("(FR,10,UP,0)");
		queue.parse("(FR,1,UP,-1)");
		queue.parse("(FR,1,UP,9999999999999)");
		queue.parse("(FR,1,UP,999999999999999999999999999999999999999)");
		queue.parse("(FR,-1,DOWN,0)");
		queue.parse("(FR,12,UP,0)");
		queue.parse("(FR,0,UP,5)");
		queue.parse(
				"(FR,000000000000000000000000000000000000000000000000000000000000000000000000000000000000001,UP,0)");
		queue.parse("(FR,111111111111111111111111111111111111111111,UP,0)");
		assertEquals(0, queue.getSize());
		queue.parse("(FR,1,UP,0)");
		queue.parse("(FR,5,UP,3)");
		queue.parse("(FR,5,UP,1)");
		assertEquals(2, queue.getSize());
		queue = new Queue();
		queue.parse("(ER,0,0)");
		queue.parse("(ER,-1,0)");
		queue.parse("(ER,11,0)");
		queue.parse("(ER,-1,0)");
		queue.parse("(ER,11,0)");
		queue.parse("(ER,1,-1)");
		queue.parse("(ER,1,99999999999999999)");
		queue.parse("(ER,1,0)");
		queue.parse("(ER,1,1)");
		assertEquals(0, queue.getSize());
		queue.parse("(FR,1,UP,0)");
		queue.parse("(ER,1,15)");
		queue.parse("(ER,1,5)");
		assertEquals(2, queue.getSize());
		Request request = new Request(Requester.FR, 2, 2, 2);
		request.equals(request);
		request.equals(new Object());
		try {
			queue.repOK();
			Class<?> classType = queue.getClass();
			Field a = classType.getDeclaredField("rqList");
			Field b = classType.getDeclaredField("validity");
			a.setAccessible(true); // 抑制Java对修饰符的检查
			b.setAccessible(true); // 抑制Java对修饰符的检查
			a.set(queue, null);
			queue.repOK();
			a.set(queue, new Request[5]);
			b.set(queue, null);
			queue.repOK();
			a.set(queue, null);
			b.set(queue, null);
			queue.repOK();
			// assertEquals(0, rq.getcnt());//assert rq.getcnt() == 0;
			throw new Exception(""); 
		} catch (Exception e) {
		}

	}

	@Test
	public void test2() {
		cnt++;
		Queue queue = new Queue();
		queue.parse("(FR,1,UP,0)");
		queue.parse("(ER,5,3)");
		queue.parse("(ER,2,4)");
		queue.parse("(ER,1,6)");
		queue.parse("(FR,2,UP,9)");
		queue.parse("(FR,10,DOWN,13)");
		queue.parse("(ER,4,13)");
		queue.parse("(FR,6,DOWN,13)"); 
		queue.parse("(ER,5,14)");
		queue.parse("(FR,8,DOWN,16)");
		queue.parse("(FR,7,UP,19)");
		queue.parse("(FR,6,UP,20)");
		queue.parse("(ER,1,25)");
		queue.parse("(FR,6,DOWN,26)");
		queue.parse("(ER,3,26)");
		queue.parse("(ER,6,28)");
		queue.parse("(FR,6,DOWN,28)");
		queue.parse("(FR,10,DOWN,29)");
		queue.parse("(FR,7,DOWN,30)");
		queue.parse("(FR,3,UP,32)");
		queue.parse("(ER,9,32)");
		queue.parse("(FR,3,UP,36)");
		queue.parse("(FR,1,UP,40)");
		queue.parse("(ER,9,41)");
		queue.parse("(ER,8,41)");
		queue.parse("(ER,1,41)");
		queue.parse("(FR,9,DOWN,43)");
		queue.parse("(ER,7,43)");
		queue.parse("(FR,1,UP,44)");
		queue.parse("(FR,2,DOWN,45)");
		queue.parse("(ER,5,46)");
		queue.parse("(FR,2,UP,47)");
		queue.parse("(FR,9,DOWN,47)");
		queue.parse("(FR,8,UP,47)");
		queue.parse("(FR,2,DOWN,49)");
		queue.parse("(FR,10,DOWN,49)");
		queue.parse("(ER,10,50)");
		queue.parse("(ER,3,52)");
		queue.parse("(ER,2,53)");
		queue.parse("(FR,10,DOWN,55)");
		queue.parse("(ER,1,55)");
		queue.parse("(FR,9,DOWN,56)");
		queue.parse("(ER,1,59)");
		queue.parse("(FR,1,UP,62)");
		queue.parse("(FR,9,UP,64)");
		queue.parse("(FR,4,UP,64)");
		queue.parse("(ER,3,67)");
		queue.parse("(ER,3,69)");
		queue.parse("(FR,10,DOWN,69)");
		queue.parse("(ER,3,71)");
		queue.parse("(ER,1,73)");
		queue.parse("(FR,3,UP,74)");
		queue.parse("(ER,8,74)");
		queue.parse("(FR,9,DOWN,74)");
		queue.parse("(ER,8,75)");
		queue.parse("(ER,8,76)");
		queue.parse("(FR,8,DOWN,76)");
		queue.parse("(ER,8,78)");
		queue.parse("(ER,3,78)");
		queue.parse("(ER,4,80)");
		queue.parse("(ER,4,82)");
		queue.parse("(ER,8,84)");
		queue.parse("(FR,3,UP,84)");
		queue.parse("(ER,10,84)");
		queue.parse("(FR,4,DOWN,84)");
		queue.parse("(FR,4,DOWN,84)");
		queue.parse("(ER,8,85)");
		queue.parse("(FR,2,DOWN,88)");
		queue.parse("(ER,10,90)");
		queue.parse("(ER,5,90)");
		queue.parse("(ER,7,94)");
		queue.parse("(ER,7,94)");
		queue.parse("(FR,8,UP,96)");
		queue.parse("(ER,7,96)");
		queue.parse("(ER,8,97)");
		queue.parse("(FR,5,UP,99)");
		queue.parse("(FR,8,UP,102)");
		queue.parse("(ER,7,103)");
		queue.parse("(ER,5,103)");
		queue.parse("(ER,7,106)");
		queue.parse("(ER,6,106)");
		queue.parse("(ER,6,106)");
		queue.parse("(ER,2,108)");
		queue.parse("(ER,8,108)");
		queue.parse("(FR,7,UP,110)");
		queue.parse("(FR,5,DOWN,114)");
		queue.parse("(ER,1,115)");
		queue.parse("(FR,8,DOWN,116)");
		queue.parse("(FR,3,UP,118)");
		queue.parse("(ER,7,119)");
		queue.parse("(ER,1,119)");
		queue.parse("(ER,7,119)");
		queue.parse("(ER,5,122)");
		queue.parse("(FR,2,DOWN,127)");
		queue.parse("(FR,8,DOWN,128)");
		queue.parse("(FR,5,UP,128)");
		queue.parse("(FR,6,DOWN,130)");
		queue.parse("(FR,5,DOWN,134)");
		queue.parse("(FR,5,DOWN,134)");
		queue.parse("(FR,5,DOWN,134)");
		queue.parse("RUN");
		// assertEquals(0, rq.getcnt());//assert rq.getcnt() == 0;
		SubScheduler scheduler = new SubScheduler();
		scheduler.repOK();
		Elevator elevator = new Elevator();
		elevator.repOK();
		while (!queue.end()) {
			scheduler.command(queue, elevator);
		}
		assertEquals(0, queue.getSize());
	}

	@Test
	public void test3() {
		cnt++;
		Queue queue = new Queue();
		queue.parse("(FR,1,UP,0)");
		queue.parse("(ER,5,3)");
		queue.parse("(ER,2,4)");
		queue.parse("(ER,1,6)");
		queue.parse("(FR,2,UP,9)");
		queue.parse("(FR,10,DOWN,13)");
		queue.parse("(ER,4,13)");
		queue.parse("(FR,6,DOWN,13)");
		queue.parse("(ER,5,14)");
		queue.parse("(FR,8,DOWN,16)");
		queue.parse("(FR,7,UP,19)");
		queue.parse("(FR,6,UP,20)");
		queue.parse("(ER,1,25)");
		queue.parse("(FR,6,DOWN,26)");
		queue.parse("(ER,3,26)");
		queue.parse("(ER,6,28)");
		queue.parse("(FR,6,DOWN,28)");
		queue.parse("(FR,10,DOWN,29)");
		queue.parse("(FR,7,DOWN,30)");
		queue.parse("(FR,3,UP,32)");
		queue.parse("(ER,9,32)");
		queue.parse("(FR,3,UP,36)");
		queue.parse("(FR,1,UP,40)");
		queue.parse("(ER,9,41)");
		queue.parse("(ER,8,41)");
		queue.parse("(ER,1,41)");
		queue.parse("(FR,9,DOWN,43)");
		queue.parse("(ER,7,43)");
		queue.parse("(FR,1,UP,44)");
		queue.parse("(FR,2,DOWN,45)");
		queue.parse("(ER,5,46)");
		queue.parse("(FR,2,UP,47)");
		queue.parse("(FR,9,DOWN,47)");
		queue.parse("(FR,8,UP,47)");
		queue.parse("(FR,2,DOWN,49)");
		queue.parse("(FR,10,DOWN,49)");
		queue.parse("(ER,10,50)");
		queue.parse("(ER,3,52)");
		queue.parse("(ER,2,53)");
		queue.parse("(FR,10,DOWN,55)");
		queue.parse("(ER,1,55)");
		queue.parse("(FR,9,DOWN,56)");
		queue.parse("(ER,1,59)");
		queue.parse("(FR,1,UP,62)");
		queue.parse("(FR,9,UP,64)");
		queue.parse("(FR,4,UP,64)");
		queue.parse("(ER,3,67)");
		queue.parse("(ER,3,69)");
		queue.parse("(FR,10,DOWN,69)");
		queue.parse("(ER,3,71)");
		queue.parse("(ER,1,73)");
		queue.parse("(FR,3,UP,74)");
		queue.parse("(ER,8,74)");
		queue.parse("(FR,9,DOWN,74)");
		queue.parse("(ER,8,75)");
		queue.parse("(ER,8,76)");
		queue.parse("(FR,8,DOWN,76)");
		queue.parse("(ER,8,78)");
		queue.parse("(ER,3,78)");
		queue.parse("(ER,4,80)");
		queue.parse("(ER,4,82)");
		queue.parse("(ER,8,84)");
		queue.parse("(FR,3,UP,84)");
		queue.parse("(ER,10,84)");
		queue.parse("(FR,4,DOWN,84)");
		queue.parse("(FR,4,DOWN,84)");
		queue.parse("(ER,8,85)");
		queue.parse("(FR,2,DOWN,88)");
		queue.parse("(ER,10,90)");
		queue.parse("(ER,5,90)");
		queue.parse("(ER,7,94)");
		queue.parse("(ER,7,94)");
		queue.parse("(FR,8,UP,96)");
		queue.parse("(ER,7,96)");
		queue.parse("(ER,8,97)");
		queue.parse("(FR,5,UP,99)");
		queue.parse("(FR,8,UP,102)");
		queue.parse("(ER,7,103)");
		queue.parse("(ER,5,103)");
		queue.parse("(ER,7,106)");
		queue.parse("(ER,6,106)");
		queue.parse("(ER,6,106)");
		queue.parse("(ER,2,108)");
		queue.parse("(ER,8,108)");
		queue.parse("(FR,7,UP,110)");
		queue.parse("(FR,5,DOWN,114)");
		queue.parse("(ER,1,115)");
		queue.parse("(FR,8,DOWN,116)");
		queue.parse("(FR,3,UP,118)");
		queue.parse("(ER,7,119)");
		queue.parse("(ER,1,119)");
		queue.parse("(ER,7,119)");
		queue.parse("(ER,5,122)");
		queue.parse("(FR,2,DOWN,127)");
		queue.parse("(FR,8,DOWN,128)");
		queue.parse("(FR,5,UP,128)");
		queue.parse("(FR,6,DOWN,130)");
		queue.parse("(FR,5,DOWN,134)");
		queue.parse("RUN");
		//assert rq.getcnt() == 0;
		SubScheduler scheduler = new SubScheduler();
		scheduler.repOK();
		Elevator elevator = new Elevator();
		elevator.repOK();
		while (!queue.end()) {
			scheduler.command(queue, elevator);
		}
		assertEquals(0, queue.getSize());
		assertEquals(5, elevator.getFloor());
		assertEquals(140.5, elevator.getClock(), 0); 
	}
	@Test
	public void test4() {
		cnt++;
		Queue queue = new Queue();
		queue.parse("(FR,1,UP,0)");
		queue.parse("(ER,5,0)");
		queue.parse("(FR,5,UP,3)");
		queue.parse("(FR,5,DOWN,4)");
		queue.parse("(ER,8,6)");
		queue.parse("(ER,6,7)");
		queue.parse("(FR,7,DOWN,10)");
		queue.parse("(FR,4,UP,13)");
		queue.parse("(FR,7,UP,15)");
		queue.parse("(ER,7,16)");
		queue.parse("(ER,10,19)");
		queue.parse("(FR,10,DOWN,21)");
		queue.parse("(FR,2,UP,24)");
		queue.parse("(ER,5,27)");
		queue.parse("(ER,4,28)");
		queue.parse("(FR,5,UP,31)");
		queue.parse("(ER,10,34)");
		queue.parse("(ER,10,35)");
		queue.parse("(FR,6,UP,36)");
		queue.parse("(FR,7,UP,36)");
		queue.parse("(FR,9,UP,36)");
		queue.parse("(ER,10,36)");
		queue.parse("(FR,7,DOWN,38)");
		queue.parse("(ER,10,40)");
		queue.parse("(ER,5,40)");
		queue.parse("(FR,7,UP,40)");
		queue.parse("(ER,5,40)");
		queue.parse("(ER,4,40)");
		queue.parse("(ER,9,42)");
		queue.parse("(FR,2,DOWN,44)");
		queue.parse("(ER,9,45)");
		queue.parse("(FR,8,UP,48)");
		queue.parse("(ER,2,50)");
		queue.parse("(ER,3,52)");
		queue.parse("(FR,8,DOWN,52)");
		queue.parse("(ER,7,53)");
		queue.parse("(FR,7,DOWN,53)");
		queue.parse("(ER,5,54)");
		queue.parse("(ER,1,57)");
		queue.parse("(FR,5,UP,57)");
		queue.parse("(FR,2,DOWN,58)");
		queue.parse("(ER,8,61)");
		queue.parse("(ER,6,62)");
		queue.parse("(ER,9,65)");
		queue.parse("(ER,3,66)");
		queue.parse("(FR,2,UP,68)");
		queue.parse("(ER,10,68)");
		queue.parse("(FR,9,DOWN,70)");
		queue.parse("(ER,3,71)");
		queue.parse("(FR,7,UP,72)");
		queue.parse("(FR,2,UP,75)");
		queue.parse("(ER,5,75)");
		queue.parse("(ER,4,75)");
		queue.parse("(ER,9,77)");
		queue.parse("(FR,8,DOWN,79)");
		queue.parse("(FR,9,DOWN,79)");
		queue.parse("(ER,1,80)");
		queue.parse("(ER,5,81)");
		queue.parse("(ER,6,82)");
		queue.parse("(ER,10,82)");
		queue.parse("(ER,9,82)");
		queue.parse("(ER,9,82)");
		queue.parse("(ER,4,83)");
		queue.parse("(ER,8,84)");
		queue.parse("(FR,9,UP,87)");
		queue.parse("(FR,1,UP,90)");
		queue.parse("(ER,3,93)");
		queue.parse("(FR,3,UP,94)");
		queue.parse("(ER,8,96)");
		queue.parse("(FR,6,UP,98)");
		queue.parse("(FR,3,UP,101)");
		queue.parse("(FR,9,UP,104)");
		queue.parse("(FR,6,DOWN,107)");
		queue.parse("(ER,5,109)");
		queue.parse("(FR,9,UP,111)");
		queue.parse("(ER,10,112)");
		queue.parse("(ER,3,113)");
		queue.parse("(ER,3,113)");
		queue.parse("(FR,4,DOWN,116)");
		queue.parse("(ER,2,118)");
		queue.parse("(ER,4,118)");
		queue.parse("(FR,3,DOWN,121)");
		queue.parse("(FR,8,DOWN,122)");
		queue.parse("(ER,7,124)");
		queue.parse("(ER,3,125)");
		queue.parse("(ER,5,125)");
		queue.parse("(ER,3,128)");
		queue.parse("(ER,9,129)");
		queue.parse("(ER,4,129)");
		queue.parse("(ER,8,132)");
		queue.parse("(ER,8,132)");
		queue.parse("(FR,4,DOWN,133)");
		queue.parse("(ER,7,134)");
		queue.parse("(ER,3,134)");
		queue.parse("(ER,5,134)");
		queue.parse("(ER,2,136)");
		queue.parse("(FR,4,DOWN,136)");
		queue.parse("(ER,3,139)");
		queue.parse("(FR,6,DOWN,140)");
		queue.parse("(FR,9,UP,140)");
		queue.parse("RUN");
		//assert rq.getcnt() == 0;
		SubScheduler scheduler = new SubScheduler();
		scheduler.repOK();
		Elevator elevator = new Elevator();
		elevator.repOK();
		while (!queue.end()) {
			scheduler.command(queue, elevator);
		}
		assertEquals(0, queue.getSize());
		assertEquals(9, elevator.getFloor());
		assertEquals(148, elevator.getClock(), 0); 
	}
 
	@Test
	public void test5() {
		cnt++;
		Queue queue = new Queue();
		queue.parse("(FR,1,UP,0)");
		queue.parse("(FR,5,UP,3)");
		queue.parse("(FR,9,DOWN,5)");
		queue.parse("(FR,9,DOWN,5)");
		queue.parse("(FR,9,UP,5)");
		queue.parse("(ER,9,5)");
		queue.parse("(FR,5,DOWN,20)");
		queue.parse("(FR,5,UP,20)");
		queue.parse("(ER,5,20)");
		queue.parse("(FR,9,DOWN,35)");
		queue.parse("(FR,7,UP,35)");
		queue.parse("(FR,8,UP,35)");
		// assertEquals(0, rq.getcnt());//assert rq.getcnt() == 0;
		SubScheduler scheduler = new SubScheduler();
		Elevator elevator = new Elevator();
		while (!queue.end()) {
			scheduler.command(queue, elevator);
		}
	}
	
	@Test
	public void test6() {
		cnt++;
		Direction.values(); 
		Direction.valueOf("UP");
		Requester.values();
		Requester.valueOf("FR");
		Request requestOne = new Request(Requester.FR, 7, Direction.UP, 110, 0);
		Request requestTwo = new Request(Requester.FR, 7, Direction.UP, 109, 0);
		assertEquals(false, requestOne.equals(requestTwo)); 
	}
}
