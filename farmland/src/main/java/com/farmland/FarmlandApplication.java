package com.farmland;

import java.io.Console;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.awt.Point;

import com.barrenland.BarrenLand;
import com.enums.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;

//@SpringBootApplication
public class FarmlandApplication{

	//@Autowired
	final int xMax = 400;
	final int yMax = 600;

	//private InputSanitization rawInput = new InputSanitization();
	private LandMarker[][] Farmland = new LandMarker[xMax][yMax];
	private LinkedList<Point> coordQueue = new LinkedList<Point>();
	private List<BarrenLand> barrenLandList = new LinkedList<BarrenLand>();
	private LinkedList<Integer> FarmfieldAreas = new LinkedList<Integer>();
	static Console console;

	public static void main(String[] args) {
		//SpringApplication.run(FarmlandApplication.class, args);
		FarmlandApplication instance = new FarmlandApplication();
		
		console = System.console();
		//We want to start off with assuming the entire 2d array is farmable, and apply the barren land later
		Arrays.stream(instance.Farmland).forEach(a -> Arrays.fill(a,LandMarker.Farmable));
		ReturnTypes retCode = ReturnTypes.RTN_ERROR;
		String input;
		do{
			input = console.readLine("Please enter the set of rectangles for the barren land\n"
			+ "Example input: {\"10 20 30 40\", \"110 120 130 140\"} \n\n");
			retCode = instance.sanitizeInput(input);
			if (ReturnTypes.RTN_OK == retCode){
				instance.grabRectangles(input);
			}
		}while(ReturnTypes.RTN_ERROR == retCode);

		instance.drawBarrenLandRectangles();

		Iterator<BarrenLand> itr = instance.barrenLandList.iterator();
		while(itr.hasNext()){
			instance.traverseBarrenRectangleBorder(itr.next());
			itr.remove();
		}
		
		Collections.sort(instance.FarmfieldAreas);

		instance.outputResults();
	}

	/**
	 * Output the area of the farmable areas to the console
	 */
	private void outputResults(){
		console.printf("\n");
		while(FarmfieldAreas.size() != 0){
			console.printf(FarmfieldAreas.pop() + " ");//FarmfieldAreas.pop();
		}

	}

	/** Calculates the area of the farmable land that the coorinate supplied is a part of
	 * @param x - x coodinate for the space to check the area of the farmable space that coordinate is a part of
	 * @param y - y coodinate for the space to check the area of the farmable space that coordinate is a part of
	 */
	private void calculateFarmableLandArea(int x, int y){
		//We only take action if the coordinate is farmable, if its not, we skip it
		if(Farmland[x][y] == LandMarker.Farmable){
			coordQueue.add(new Point(x, y));
			Farmland[x][y] = LandMarker.InQueue;
			int currentArea = findEntireFarmableArea();
			FarmfieldAreas.add(currentArea);
		}
	}

	/**
	 * Traverse the border of a given rectangle, looking at the spaces next to the rectangle for farmable coordinates. If a farmable coordinate is found, determine the total size of the farmable space that the coordinate is a part of  
	 * @param rec - Rectangle to traverse along the border of
	 */
	private void traverseBarrenRectangleBorder(BarrenLand rec){
		//go along the border of a rectangle and check to see if the spot just outside of the rectangle is farmable. if it is, and has not been taken care of yet, run calculateFarmableLandArea() on it, otherwise, keep moving. Once you have checked all spots around the 1 space, mark it as checked and move on. If you hit a spot that has already been marked checked, just keep going, since it was likely an intersection between multiple rectangles
		int x1 = rec.GetX1();
		int x2 = rec.GetX2();
		int y1 = rec.GetY1();
		int y2 = rec.GetY2();

		
		//traverse the y1 wall and x1, y1 corner
		if(y1 > 0 && y1 <= yMax - 1){

			if(Farmland[x1][y1-1] == LandMarker.Farmable){
				calculateFarmableLandArea(x1, y1 - 1);
			}

			if(x1 > 0 && x1 <= xMax - 1){
				if(Farmland[x1-1][y1-1] == LandMarker.Farmable){
					calculateFarmableLandArea(x1 - 1, y1 - 1);
				}
				if(Farmland[x1-1][y1] == LandMarker.Farmable){
					calculateFarmableLandArea(x1 - 1, y1);
				}
			}

			for(int i = x1+1; i < x2; i++){
				if(Farmland[i][y1-1] == LandMarker.Farmable){
					calculateFarmableLandArea(i, y1 - 1);
				}
			}
		}

		//traverse the x2 wall and x2, y1 corner
		if(x2 >= 0 && x2 < xMax - 1){
			if(Farmland[x2+1][y1] == LandMarker.Farmable){
				calculateFarmableLandArea(x2 + 1, y1);
			}

			if(y1 > 0 && y1 <= yMax - 1){
				if(Farmland[x2+1][y1-1] == LandMarker.Farmable){
					calculateFarmableLandArea(x2 + 1, y1 - 1);
				}
				if(Farmland[x2][y1-1] == LandMarker.Farmable){
					calculateFarmableLandArea(x2, y1 - 1);
				}
			}

			for(int i = y1+1; i < y2; i++){
				if(Farmland[x2+1][i] == LandMarker.Farmable){
					calculateFarmableLandArea(x2+1, i);
				}
			}
		}

		//traverse the y2 wall and x2, y2 corner
		if(y2 >= 0 && y2 < yMax - 1){

			if(Farmland[x2][y2+1] == LandMarker.Farmable){
				calculateFarmableLandArea(x2, y2 + 1);
			}
			
			if(x2 >= 0 && x2 < xMax - 1){
				if(Farmland[x2+1][y2+1] == LandMarker.Farmable){
					calculateFarmableLandArea(x2 + 1, y2 + 1);
				}
				if(Farmland[x2+1][y2] == LandMarker.Farmable){
					calculateFarmableLandArea(x2 + 1, y2);
				}
			}

			for(int i = x2-1; i > x1; i--){
				if(Farmland[i][y2+1] == LandMarker.Farmable){
					calculateFarmableLandArea(i, y2 + 1);
				}
			}
		}

		//traverse the x1 wall and x1, y2 corner
		if(x1 > 0 && x1 <= xMax - 1){

			if(Farmland[x1-1][y2] == LandMarker.Farmable){
				calculateFarmableLandArea(x1-1, y2);
			}
			
			if(y2 >= 0 && y2 < yMax - 1){
				if(Farmland[x1-1][y2+1] == LandMarker.Farmable){
					calculateFarmableLandArea(x1 - 1, y2 + 1);
				}
				if(Farmland[x1][y2+1] == LandMarker.Farmable){
					calculateFarmableLandArea(x1, y2 + 1);
				}
			}

			for(int i = y2-1; i > y1; i--){
				if(Farmland[x1-1][i] == LandMarker.Farmable){
					calculateFarmableLandArea(x1-1, i);
				}
			}
		}

	}

	//#region Input Sanitization
	/** Sanitizes input
	 * example of proper input
	 * {“48 192 351 207”, “48 392 351 407”, “120 52 135 547”, “260 52 275 547”}
	 * @param input - raw input
	 * @return - returns whether or not the input is valid or not
	 */
	public ReturnTypes sanitizeInput(String input){
		//example of proper input
		//  {“48 192 351 207”, “48 392 351 407”, “120 52 135 547”, “260 52 275 547”} 
		int curIndex;
		if ((!input.substring(0, 1).equals("{")) || (!input.substring(input.length() - 1).equals("}"))){
			console.printf("Your input is missing either a opening bracket at the start, or a closing bracket at the end.\n");
			return ReturnTypes.RTN_ERROR;
		}
		
		if(/*(-1 != input.substring(1).indexOf("{")) || */(input.length() - 1 != input.indexOf("}"))){
			//we have an extra '{' or '}' in the string, therefore it is invalid
			console.printf("There is an extra bracket in the input. Please remove it.\n");
			return ReturnTypes.RTN_ERROR;
		}

		curIndex = 1; //start at index 1 since we know index 0 is a '{'
		//Loop through, verifying that we have retangles clearly marked out, and that we have no unexpected items in the input
		while(input.length() - 1 != curIndex){
			int quote1Loc, quote2Loc;
			quote1Loc = input.indexOf("\"", curIndex);
			if(-1 == quote1Loc){
				if (input.substring(curIndex).equals("}")){
					return ReturnTypes.RTN_OK;
				}else{
					console.printf("ERROR: Please double check the input and try again \n");
					return ReturnTypes.RTN_ERROR;
				}
				
			}
			curIndex = quote1Loc + 1;
			quote2Loc = input.indexOf("\"", curIndex);
			if(-1 == quote2Loc){
				//we have an extra quote. Invalid input
				console.printf("There is an extra \" in the input. Please remove it.\n");
				return ReturnTypes.RTN_ERROR;
			}
			//grab the text between the 2 quotes and verify it is a rectangle
			if(ReturnTypes.RTN_OK != verifyRectangle(input.substring(quote1Loc + 1, quote2Loc))){
				return ReturnTypes.RTN_ERROR;
			}
			curIndex = quote2Loc + 1;
		}
		//check for input with zero rectangles. Only need to check the length, since we know we start with a '{' and end with a '}'

		//grab the last characters of the string

			// do we start and end in a bracket
	   // find the start and end of a rectangle
	   //       verify we find the second quote before a comma
	   //once found, verify 4 integers inside of there
	   //move onto next
	   //repeat until we hit the end
	   
		return ReturnTypes.RTN_OK;
	}

	/** Used to verify we have the expected input for each rectangle
	 * example: '1 2 3 4' but exclude the '
	 * @param rectangle - input string we expect to be a rectangle
	 * @return returns RTN_OK if the input has a similar format to the example above
	 */
	private ReturnTypes verifyRectangle(String rectangle){
		String num;
		int curIndex = 0;
		
		for(int i = 1; i <=3; i++){
			num = rectangle.substring(curIndex, rectangle.indexOf(" ", curIndex));
			if(ReturnTypes.RTN_OK != verifyInteger(num)){
				return ReturnTypes.RTN_ERROR;
			}
			curIndex = rectangle.indexOf(" ", curIndex) + 1;
		}

		if(ReturnTypes.RTN_OK != verifyInteger(rectangle.substring(curIndex))){
			return ReturnTypes.RTN_ERROR;
		}

		return ReturnTypes.RTN_OK;
	}

	/** Verify that the string contains an integer
	 * @param num - string to check to see if it can be parsed into an integer
	 * @return - return OK if we are able to parse an integer from it
	 */
	private ReturnTypes verifyInteger(String num){
		try{
			Integer.parseInt(num);
		}catch(NumberFormatException e){
			console.printf("ERROR: Rectangle input is not correct. Please double check the input and try again\n");
			return ReturnTypes.RTN_ERROR;
		}
		return ReturnTypes.RTN_OK;
	}
	//#endregion
	
	/**
	 * parse through the input string and find all the rectangles in the string, then save each to barrenLandList
	 * @param input - raw input from user. This function assumes this has been sanitized
	 * @return - RTN_ERROR if there was an issue, else RTN_OK
	 */
	private ReturnTypes grabRectangles(String input){
		int x1, y1, x2, y2;
		int curIndex = 0;
		int substrIndex;
		int quote1Loc, quote2Loc;
		String rectangle;

		while(curIndex != input.length()){
			quote1Loc = input.indexOf("\"", curIndex);
			quote2Loc = input.indexOf("\"", quote1Loc + 1);
			
			substrIndex = 0;

			if(-1 == quote1Loc){
				//we dont have any rectangles
				return ReturnTypes.RTN_OK;
			}
	
			rectangle = input.substring(quote1Loc + 1, quote2Loc);
			curIndex = quote2Loc + 1;
	
			x1 = Integer.parseInt(rectangle.substring(substrIndex, rectangle.indexOf(" ", substrIndex)));
			substrIndex = rectangle.indexOf(" ", substrIndex) + 1;
			y1 = Integer.parseInt(rectangle.substring(substrIndex, rectangle.indexOf(" ", substrIndex)));
			substrIndex = rectangle.indexOf(" ", substrIndex) + 1;
			x2 = Integer.parseInt(rectangle.substring(substrIndex, rectangle.indexOf(" ", substrIndex)));
			substrIndex = rectangle.indexOf(" ", substrIndex) + 1;
			y2 = Integer.parseInt(rectangle.substring(substrIndex));
	
			//checking to make sure our coordinates are withing the bounds of our farmable area
			if((xMax <= x1) || (0 > x1) || (xMax <= x2) || (0 > x2) ||
				(yMax <= y1) || (0 > y1) || (yMax <= y2) || (0 > y2)){
					console.printf("ERROR: Rectangle goes out of bounds. Please double check the input and try again\n");
				return ReturnTypes.RTN_ERROR;
			}
	
			if(x1 > x2){
				int temp = x1;
				x1 = x2;
				x2 = temp;
			}
	
			if(y1 > y2){
				int temp = y1;
				y1 = y2;
				y2 = temp;
			}
	
			barrenLandList.add(new BarrenLand(x1, y1, x2, y2));
		}
		
		return ReturnTypes.RTN_OK;
	}

	/**
	 * Calls drawBarrenLandRectangle(BarrenLand) on each of the items in barrenLandList to mark the cooresponding spots as LandMarker.Barren
	 */
	private void drawBarrenLandRectangles(){
		Iterator<BarrenLand> itr = barrenLandList.iterator();
		while(itr.hasNext()){
			drawBarrenLandRectangle(itr.next());
		}
	}

	/**
	 * Draws a single rectangle of barren land onto Farmland[][]
	 * @param barren - rectangle to draw onto Farmland[][]
	 */
	private void drawBarrenLandRectangle(BarrenLand barren){
		for(int x = barren.GetX1(); x <= barren.GetX2(); x++){
			for(int y = barren.GetY1(); y <= barren.GetY2(); y++){
				Farmland[x][y] = LandMarker.Barren;
			}
		}
	}

	/**
	 * Looks at the coordinates directly above, below, and to the left and right.
	 * If they are farmable land that has not yet been looked at, nor is in our queue already, it adds them to the queue of points to be checked and counted
	 * @param x - X coordinate of the point we are looking at
	 * @param y - Y coordinate of the point we are looking at
	 */
	private void enqueueSurroundingCoordinates(int x, int y){

		if(x != 0){
			if(LandMarker.Farmable == Farmland[x-1][y]){
				coordQueue.add(new Point(x-1, y));
				Farmland[x-1][y] = LandMarker.InQueue;
			}
		}
		if(x != xMax - 1){
			if(LandMarker.Farmable == Farmland[x+1][y]){
				coordQueue.add(new Point(x+1, y));
				Farmland[x+1][y] = LandMarker.InQueue;
			}
		}
		if(y != 0){
			if(LandMarker.Farmable == Farmland[x][y-1]){
				coordQueue.add(new Point(x, y-1));
				Farmland[x][y-1] = LandMarker.InQueue;
			}
		}
		if(y != yMax - 1){
			if(LandMarker.Farmable == Farmland[x][y+1]){
				coordQueue.add(new Point(x, y+1));
				Farmland[x][y+1] = LandMarker.InQueue;
			}
		}
	}

	/**
	 * finds the entire farmable area of each item in coordQueue. This function expects there to be coordinates from a single farmable area in coordQueue, not from 2 or more seperate area.
	 * @return
	 */
	private int findEntireFarmableArea(){
		Point currentPoint;
		int curArea = 0;
		while(coordQueue.size() != 0){
			currentPoint = coordQueue.peek();
			enqueueSurroundingCoordinates(currentPoint.x, currentPoint.y);
			curArea++;
			coordQueue.pop();
			Farmland[currentPoint.x][currentPoint.y] = LandMarker.Farmable_Marked;
		}
		
		return curArea;
	}
}
