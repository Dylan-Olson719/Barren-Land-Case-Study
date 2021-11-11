package com.example.farmland;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import com.barrenland.BarrenLand;
import com.enums.LandMarker;
import com.enums.ReturnTypes;
import com.farmland.FarmlandApplication;

import org.assertj.core.api.Assert;
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
		assertTrue(ReturnTypes.RTN_OK == obj.sanitizeInput("{}"));
		assertTrue(ReturnTypes.RTN_OK == obj.sanitizeInput("{\"48 192 351 207\", \"48 392 351 407\", \"120 52 135 547\", \"260 52 275 547\"}"));
		assertTrue(ReturnTypes.RTN_OK == obj.sanitizeInput("{\"0 292 399 307\"}"));
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{\"1 2 3 4}")); //missing parenthesis
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{1 2 3 4}")); //no parentheses
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{1 2 3 4\"}")); //missing parenthesis
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{\"1 2 }3 4\"}")); //extra closing bracket
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{\"1 2 {3 4\"}")); //extra opening bracket
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{\"1 2 3\"}")); //only 3 parts to the rectangle
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{\"1.1 2.2 3.3 4.4\"}")); //entering doubles rather than integers
		assertTrue(ReturnTypes.RTN_ERROR == obj.sanitizeInput("{\"a b c d\"}")); //entering characters rather than numbers

		assertTrue(ReturnTypes.RTN_OK == obj.grabRectangles("{\"1 2 3 4\"}"));
		assertTrue(ReturnTypes.RTN_OK == obj.grabRectangles("{\"48 192 351 207\", \"48 392 351 407\", \"120 52 135 547\", \"260 52 275 547\"}"));
		assertTrue(ReturnTypes.RTN_ERROR == obj.grabRectangles("{\"0 292 400 307\"}")); //over the max for the x coordinate
		assertTrue(ReturnTypes.RTN_ERROR == obj.grabRectangles("{\"0 292 399 600\"}")); //over the max for the y coordinate
		assertTrue(ReturnTypes.RTN_ERROR == obj.grabRectangles("{\"-1 292 399 307\"}")); //negative number entered
	}

	@Test
	public void testBarrenLandDrawing(){
		obj = new FarmlandApplication();
		Arrays.stream(obj.Farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));

		obj.drawBarrenLandRectangle(smallBarrenLand);
		obj.drawBarrenLandRectangle(mediumBarrenLand);
		obj.drawBarrenLandRectangle(largeBarrenLand);
		assertTrue(LandMarker.Barren == obj.Farmland[1][1]);
		assertTrue(LandMarker.Barren == obj.Farmland[10][10]);
		assertTrue(LandMarker.Barren == obj.Farmland[10][20]);
		assertTrue(LandMarker.Barren == obj.Farmland[20][20]);
		assertTrue(LandMarker.Barren == obj.Farmland[20][10]);
		assertTrue(LandMarker.Barren == obj.Farmland[100][100]);
		assertTrue(LandMarker.Barren == obj.Farmland[100][200]);
		assertTrue(LandMarker.Barren == obj.Farmland[200][200]);
		assertTrue(LandMarker.Barren == obj.Farmland[200][100]);
	}

	@Test
	public void testQueueingOfCoordinates(){
		obj = new FarmlandApplication();
		Arrays.stream(obj.Farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));

		obj.Farmland[10][10] = LandMarker.InQueue;
		obj.Farmland[9][10] = LandMarker.InQueue;//tests if we will get duplicates in the queue

		obj.Farmland[20][20] = LandMarker.InQueue;
		obj.Farmland[20][19] = LandMarker.Barren; // tests if barren land gets into the queue

		obj.Farmland[30][30] = LandMarker.InQueue;
		obj.Farmland[31][30] = LandMarker.Farmable_Marked; //tests if Farmable_Marked gets into the queue

		obj.enqueueSurroundingCoordinates(10,10);
		obj.enqueueSurroundingCoordinates(20,20);
		obj.enqueueSurroundingCoordinates(30,30);

		assertTrue(LandMarker.InQueue == obj.Farmland[11][10]);
		assertTrue(LandMarker.InQueue == obj.Farmland[10][9]);
		assertTrue(LandMarker.InQueue == obj.Farmland[10][11]);
		assertTrue(LandMarker.InQueue == obj.Farmland[19][20]);
		assertTrue(LandMarker.InQueue == obj.Farmland[21][20]);
		assertTrue(LandMarker.InQueue == obj.Farmland[20][21]);
		assertTrue(LandMarker.InQueue == obj.Farmland[29][30]);
		assertTrue(LandMarker.InQueue == obj.Farmland[30][29]);
		assertTrue(LandMarker.InQueue == obj.Farmland[30][31]);
		assertTrue(LandMarker.InQueue == obj.Farmland[9][10]);
		assertTrue(LandMarker.Barren == obj.Farmland[20][19]);
		assertTrue(LandMarker.Farmable_Marked == obj.Farmland[31][30]);

		assertTrue(obj.coordQueue.size() == 9);
		//Check to make sure the LandMarker.InQueue item is not in the queue
		assertTrue((obj.coordQueue.peek().x == 11) && (obj.coordQueue.peek().y == 10));
		obj.coordQueue.pop();
		assertTrue((obj.coordQueue.peek().x == 10) && (obj.coordQueue.peek().y == 9));
		obj.coordQueue.pop();
		assertTrue((obj.coordQueue.peek().x == 10) && (obj.coordQueue.peek().y == 11));
		obj.coordQueue.pop();

		//Check to make sure the LandMarker.Barren item is not in the queue
		assertTrue((obj.coordQueue.peek().x == 19) && (obj.coordQueue.peek().y == 20));
		obj.coordQueue.pop();
		assertTrue((obj.coordQueue.peek().x == 21) && (obj.coordQueue.peek().y == 20));
		obj.coordQueue.pop();
		assertTrue((obj.coordQueue.peek().x == 20) && (obj.coordQueue.peek().y == 21));
		obj.coordQueue.pop();

		//Check to make sure the LandMarker.Farmable_Marked item is not in the queue
		assertTrue((obj.coordQueue.peek().x == 29) && (obj.coordQueue.peek().y == 30));
		obj.coordQueue.pop();
		assertTrue((obj.coordQueue.peek().x == 30) && (obj.coordQueue.peek().y == 29));
		obj.coordQueue.pop();
		assertTrue((obj.coordQueue.peek().x == 30) && (obj.coordQueue.peek().y == 31));
		obj.coordQueue.pop();

	}

	@Test
	public void testCalculateFarmableLandArea(){
		obj = new FarmlandApplication();
		obj2 = new FarmlandApplication();
		obj3 = new FarmlandApplication();

		Arrays.stream(obj.Farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));
		Arrays.stream(obj2.Farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));
		Arrays.stream(obj3.Farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));

		obj.calculateFarmableLandArea(0,0);
		assertTrue(obj.FarmfieldAreas.pop() == 240000);

		obj2.drawBarrenLandRectangle(defaultBarrenLand);
		obj2.calculateFarmableLandArea(0,0);
		obj2.calculateFarmableLandArea(399,599);
		assertTrue(obj2.FarmfieldAreas.pop() == 116800);
		assertTrue(obj2.FarmfieldAreas.pop() == 116800);

	}
}
