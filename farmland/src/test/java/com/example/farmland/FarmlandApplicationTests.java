package com.example.farmland;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import com.barrenland.BarrenLand;
import com.enums.LandMarker;
import com.enums.ReturnTypes;
import com.farmland.FarmlandApplication;

import org.junit.jupiter.api.Test;

//import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class FarmlandApplicationTests {

	FarmlandApplication obj, obj2, obj3;
	BarrenLand smallBarrenLand = new BarrenLand(1, 1, 1, 1);
	BarrenLand mediumBarrenLand = new BarrenLand(10, 10, 20, 20);
	BarrenLand largeBarrenLand = new BarrenLand(100, 100, 200, 200);
	BarrenLand allBarrenLand = new BarrenLand(0, 0, 399, 599);
	BarrenLand defaultBarrenLand = new BarrenLand(0, 292, 399, 307);

	@Test
	public void InputSanitizationTests(){
		obj = new FarmlandApplication();
		assertSame(ReturnTypes.RTN_OK, obj.sanitizeInput("{}"));
		assertSame(ReturnTypes.RTN_OK, obj.sanitizeInput("{\"48 192 351 207\", \"48 392 351 407\", \"120 52 135 547\", \"260 52 275 547\"}"));
		assertSame(ReturnTypes.RTN_OK, obj.sanitizeInput("{\"0 292 399 307\"}"));
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{\"1 2 3 4}")); //missing parenthesis
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{1 2 3 4}")); //no parentheses
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{1 2 3 4\"}")); //missing parenthesis
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{\"1 2 }3 4\"}")); //extra closing bracket
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{\"1 2 {3 4\"}")); //extra opening bracket
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{\"1 2 3\"}")); //only 3 parts to the rectangle
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{\"1.1 2.2 3.3 4.4\"}")); //entering doubles rather than integers
		assertSame(ReturnTypes.RTN_ERROR, obj.sanitizeInput("{\"a b c d\"}")); //entering characters rather than numbers

		assertSame(ReturnTypes.RTN_OK, obj.grabRectangles("{\"1 2 3 4\"}"));
		assertSame(ReturnTypes.RTN_OK, obj.grabRectangles("{\"48 192 351 207\", \"48 392 351 407\", \"120 52 135 547\", \"260 52 275 547\"}"));
		assertSame(ReturnTypes.RTN_ERROR, obj.grabRectangles("{\"0 292 400 307\"}")); //over the max for the x coordinate
		assertSame(ReturnTypes.RTN_ERROR, obj.grabRectangles("{\"0 292 399 600\"}")); //over the max for the y coordinate
		assertSame(ReturnTypes.RTN_ERROR, obj.grabRectangles("{\"-1 292 399 307\"}")); //negative number entered
	}

	@Test
	public void testBarrenLandDrawing(){
		obj = new FarmlandApplication();
		Arrays.stream(obj.farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));

		obj.drawBarrenLandRectangle(smallBarrenLand);
		obj.drawBarrenLandRectangle(mediumBarrenLand);
		obj.drawBarrenLandRectangle(largeBarrenLand);
		assertSame(LandMarker.Barren, obj.farmland[1][1]);
		assertSame(LandMarker.Barren, obj.farmland[10][10]);
		assertSame(LandMarker.Barren, obj.farmland[10][20]);
		assertSame(LandMarker.Barren, obj.farmland[20][20]);
		assertSame(LandMarker.Barren, obj.farmland[20][10]);
		assertSame(LandMarker.Barren, obj.farmland[100][100]);
		assertSame(LandMarker.Barren, obj.farmland[100][200]);
		assertSame(LandMarker.Barren, obj.farmland[200][200]);
		assertSame(LandMarker.Barren, obj.farmland[200][100]);
	}

	@Test
	public void testQueueingOfCoordinates(){
		obj = new FarmlandApplication();
		Arrays.stream(obj.farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));

		obj.farmland[10][10] = LandMarker.InQueue;
		obj.farmland[9][10] = LandMarker.InQueue;//tests if we will get duplicates in the queue

		obj.farmland[20][20] = LandMarker.InQueue;
		obj.farmland[20][19] = LandMarker.Barren; // tests if barren land gets into the queue

		obj.farmland[30][30] = LandMarker.InQueue;
		obj.farmland[31][30] = LandMarker.Farmable_Marked; //tests if Farmable_Marked gets into the queue

		obj.enqueueSurroundingCoordinates(10,10);
		obj.enqueueSurroundingCoordinates(20,20);
		obj.enqueueSurroundingCoordinates(30,30);

		assertSame(LandMarker.InQueue, obj.farmland[11][10]);
		assertSame(LandMarker.InQueue, obj.farmland[10][9]);
		assertSame(LandMarker.InQueue, obj.farmland[10][11]);
		assertSame(LandMarker.InQueue, obj.farmland[19][20]);
		assertSame(LandMarker.InQueue, obj.farmland[21][20]);
		assertSame(LandMarker.InQueue, obj.farmland[20][21]);
		assertSame(LandMarker.InQueue, obj.farmland[29][30]);
		assertSame(LandMarker.InQueue, obj.farmland[30][29]);
		assertSame(LandMarker.InQueue, obj.farmland[30][31]);
		assertSame(LandMarker.InQueue, obj.farmland[9][10]);
		assertSame(LandMarker.Barren, obj.farmland[20][19]);
		assertSame(LandMarker.Farmable_Marked, obj.farmland[31][30]);

		assertSame(9, obj.coordQueue.size());
		//Check to make sure the LandMarker.InQueue item is not in the queue
		assertTrue((obj.coordQueue.peek().x == 11) && (obj.coordQueue.peek().y == 10));
		obj.coordQueue.poll();
		assertTrue((obj.coordQueue.peek().x == 10) && (obj.coordQueue.peek().y == 9));
		obj.coordQueue.poll();
		assertTrue((obj.coordQueue.peek().x == 10) && (obj.coordQueue.peek().y == 11));
		obj.coordQueue.poll();

		//Check to make sure the LandMarker.Barren item is not in the queue
		assertTrue((obj.coordQueue.peek().x == 19) && (obj.coordQueue.peek().y == 20));
		obj.coordQueue.poll();
		assertTrue((obj.coordQueue.peek().x == 21) && (obj.coordQueue.peek().y == 20));
		obj.coordQueue.poll();
		assertTrue((obj.coordQueue.peek().x == 20) && (obj.coordQueue.peek().y == 21));
		obj.coordQueue.poll();

		//Check to make sure the LandMarker.Farmable_Marked item is not in the queue
		assertTrue((obj.coordQueue.peek().x == 29) && (obj.coordQueue.peek().y == 30));
		obj.coordQueue.poll();
		assertTrue((obj.coordQueue.peek().x == 30) && (obj.coordQueue.peek().y == 29));
		obj.coordQueue.poll();
		assertTrue((obj.coordQueue.peek().x == 30) && (obj.coordQueue.peek().y == 31));
		obj.coordQueue.poll();

	}

	@Test
	public void testCalculateFarmableLandArea(){
		obj = new FarmlandApplication();
		obj2 = new FarmlandApplication();
		obj3 = new FarmlandApplication();

		Arrays.stream(obj.farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));
		Arrays.stream(obj2.farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));
		Arrays.stream(obj3.farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));

		obj.calculateFarmableLandArea(0,0);
		assertSame(240000, obj.farmfieldAreas.remove(0));

		obj2.drawBarrenLandRectangle(defaultBarrenLand);
		obj2.calculateFarmableLandArea(0,0);
		obj2.calculateFarmableLandArea(399,599);
		assertSame(116800, obj2.farmfieldAreas.remove(0));
		assertSame(116800, obj2.farmfieldAreas.remove(0));

		obj3.drawBarrenLandRectangle(allBarrenLand);
		obj3.traverseBarrenRectangleBorder(allBarrenLand);
		assertSame(0, obj2.farmfieldAreas.remove(0));
	}
}
