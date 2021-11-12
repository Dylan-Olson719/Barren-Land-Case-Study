package com.farmland;

import java.io.Console;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.awt.Point;

import com.barrenland.BarrenLand;
import com.enums.*;

public class FarmlandApplication{

	//@Autowired
	static final int X_MAX = 400;
	static final int Y_MAX = 600;

	public LandMarker[][] farmland = new LandMarker[X_MAX][Y_MAX];
	public Queue<Point> coordQueue = new LinkedList<>();
	private List<BarrenLand> barrenLandList = new LinkedList<>();
	public List<Integer> farmfieldAreas = new LinkedList<>();
	static Console console;

	public static void main(String[] args) {
		FarmlandApplication instance = new FarmlandApplication();
		
		console = System.console();
		//We want to start off with assuming the entire 2d array is farmable, and apply the barren land later
		Arrays.stream(instance.farmland).forEach(a -> Arrays.fill(a,LandMarker.FARMABLE));
		ReturnTypes retCode;
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

		if(instance.barrenLandList.isEmpty()){
			//handle the case of no rectanlges being entered
			instance.calculateFarmableLandArea(0,0);
		}else{
			Iterator<BarrenLand> itr = instance.barrenLandList.iterator();
			while(itr.hasNext()){
				instance.traverseBarrenRectangleBorder(itr.next());
				itr.remove();
			}
		}

		if(instance.farmfieldAreas.isEmpty()){
			console.printf("\n 0");
		}else{
			Collections.sort(instance.farmfieldAreas);

			instance.outputResults();
		}
	}

	/**
	 * Output the area of the farmable areas to the console
	 */
	private void outputResults(){
		console.printf("\n");
		while(!farmfieldAreas.isEmpty()){
			console.printf(farmfieldAreas.remove(0) + " ");
		}

	}

	/** Calculates the area of the farmable land that the coorinate supplied is a part of
	 * @param x - x coodinate for the space to check the area of the farmable space that coordinate is a part of
	 * @param y - y coodinate for the space to check the area of the farmable space that coordinate is a part of
	 */
	public void calculateFarmableLandArea(int x, int y){
		//We only take action if the coordinate is farmable, if its not, we skip it
		if(farmland[x][y] == LandMarker.FARMABLE){
			coordQueue.add(new Point(x, y));
			farmland[x][y] = LandMarker.IN_QUEUE;
			int currentArea = findEntireFarmableArea();
			farmfieldAreas.add(currentArea);
		}
	}

	/**
	 * Traverse the border of a given rectangle, looking at the spaces next to the rectangle for farmable coordinates. If a farmable coordinate is found, determine the total size of the farmable space that the coordinate is a part of  
	 * @param rec - Rectangle to traverse along the border of
	 */
	public void traverseBarrenRectangleBorder(BarrenLand rec){
		int x1 = rec.getX1();
		int x2 = rec.getX2();
		int y1 = rec.getY1();
		int y2 = rec.getY2();

		//traverse the y1 wall and x1, y1 corner
		if(y1 > 0 && y1 <= Y_MAX - 1){

			if(farmland[x1][y1-1] == LandMarker.FARMABLE){
				calculateFarmableLandArea(x1, y1 - 1);
			}

			if(x1 > 0 && x1 <= X_MAX - 1){
				if(farmland[x1-1][y1-1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x1 - 1, y1 - 1);
				}
				if(farmland[x1-1][y1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x1 - 1, y1);
				}
			}

			for(int i = x1+1; i < x2; i++){
				if(farmland[i][y1-1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(i, y1 - 1);
				}
			}
		}

		//traverse the x2 wall and x2, y1 corner
		if(x2 >= 0 && x2 < X_MAX - 1){
			if(farmland[x2+1][y1] == LandMarker.FARMABLE){
				calculateFarmableLandArea(x2 + 1, y1);
			}

			if(y1 > 0 && y1 <= Y_MAX - 1){
				if(farmland[x2+1][y1-1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x2 + 1, y1 - 1);
				}
				if(farmland[x2][y1-1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x2, y1 - 1);
				}
			}

			for(int i = y1 + 1; i < y2; i++){
				if(farmland[x2+1][i] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x2 + 1, i);
				}
			}
		}

		//traverse the y2 wall and x2, y2 corner
		if(y2 >= 0 && y2 < Y_MAX - 1){

			if(farmland[x2][y2+1] == LandMarker.FARMABLE){
				calculateFarmableLandArea(x2, y2 + 1);
			}
			
			if(x2 >= 0 && x2 < X_MAX - 1){
				if(farmland[x2+1][y2+1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x2 + 1, y2 + 1);
				}
				if(farmland[x2+1][y2] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x2 + 1, y2);
				}
			}

			for(int i = x2-1; i > x1; i--){
				if(farmland[i][y2+1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(i, y2 + 1);
				}
			}
		}

		//traverse the x1 wall and x1, y2 corner
		if(x1 > 0 && x1 <= X_MAX - 1){

			if(farmland[x1-1][y2] == LandMarker.FARMABLE){
				calculateFarmableLandArea(x1 - 1, y2);
			}
			
			if(y2 >= 0 && y2 < Y_MAX - 1){
				if(farmland[x1-1][y2+1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x1 - 1, y2 + 1);
				}
				if(farmland[x1][y2+1] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x1, y2 + 1);
				}
			}

			for(int i = y2-1; i > y1; i--){
				if(farmland[x1-1][i] == LandMarker.FARMABLE){
					calculateFarmableLandArea(x1 - 1, i);
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
		int curIndex;
		if ((!input.substring(0, 1).equals("{")) || (!input.substring(input.length() - 1).equals("}"))){
			try{
				console.printf("Your input is missing either a opening bracket at the start, or a closing bracket at the end.\n");
			}catch(NullPointerException e){
				//likely to hit a null ptr exception when testing, since there is no console to print to
			}
			
			return ReturnTypes.RTN_ERROR;
		}
		
		if((input.length() - 1 != input.indexOf("}"))){
			//we have an extra '{' or '}' in the string, therefore it is invalid
			try{
				console.printf("There is an extra bracket in the input. Please remove it.\n");
			}catch(NullPointerException e){
				//likely to hit a null ptr exception when testing, since there is no console to print to
			}

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
					try{
						console.printf("ERROR: Please double check the input and try again \n");
					}catch(NullPointerException e){
						//likely to hit a null ptr exception when testing, since there is no console to print to
					}

					return ReturnTypes.RTN_ERROR;
				}
				
			}
			curIndex = quote1Loc + 1;
			quote2Loc = input.indexOf("\"", curIndex);
			if(-1 == quote2Loc){
				//we have an extra quote. Invalid input
				try{
					console.printf("There is an extra \" in the input. Please remove it.\n");
				}catch(NullPointerException e){
					//likely to hit a null ptr exception when testing, since there is no console to print to
				}
				
				return ReturnTypes.RTN_ERROR;
			}
			//grab the text between the 2 quotes and verify it is a rectangle
			if(ReturnTypes.RTN_OK != verifyRectangle(input.substring(quote1Loc + 1, quote2Loc))){
				return ReturnTypes.RTN_ERROR;
			}
			curIndex = quote2Loc + 1;
		}
	   
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
			try{
				num = rectangle.substring(curIndex, rectangle.indexOf(" ", curIndex));
			}catch(StringIndexOutOfBoundsException e){
				return ReturnTypes.RTN_ERROR;
			}

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
			try{
				console.printf("ERROR: Rectangle input is not correct. Please double check the input and try again\n");
			}catch(NullPointerException exception){
				//likely to hit a null ptr exception when testing, since there is no console to print to
			}
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
	public ReturnTypes grabRectangles(String input){
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
				//we dont have any more rectangles
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
			if((X_MAX <= x1) || (0 > x1) || (X_MAX <= x2) || (0 > x2) ||
				(Y_MAX <= y1) || (0 > y1) || (Y_MAX <= y2) || (0 > y2)){
				try{
					console.printf("ERROR: Rectangle goes out of bounds. Please double check the input and try again\n");
				}catch(NullPointerException exception){
					//likely to hit a null ptr exception when testing, since there is no console to print to
				}
					
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
	public void drawBarrenLandRectangle(BarrenLand barren){
		for(int x = barren.getX1(); x <= barren.getX2(); x++){
			for(int y = barren.getY1(); y <= barren.getY2(); y++){
				farmland[x][y] = LandMarker.BARREN;
			}
		}
	}

	/**
	 * Looks at the coordinates directly above, below, and to the left and right.
	 * If they are farmable land that has not yet been looked at, nor is in our queue already, it adds them to the queue of points to be checked and counted
	 * @param x - X coordinate of the point we are looking at
	 * @param y - Y coordinate of the point we are looking at
	 */
	public void enqueueSurroundingCoordinates(int x, int y){

		if((x != 0) && (LandMarker.FARMABLE == farmland[x-1][y])){
			coordQueue.add(new Point(x - 1, y));
			farmland[x-1][y] = LandMarker.IN_QUEUE;
		}
		if((x != X_MAX - 1) && (LandMarker.FARMABLE == farmland[x+1][y])){
			coordQueue.add(new Point(x + 1, y));
			farmland[x+1][y] = LandMarker.IN_QUEUE;
		}
		if((y != 0) && (LandMarker.FARMABLE == farmland[x][y-1])){
			coordQueue.add(new Point(x, y - 1));
			farmland[x][y-1] = LandMarker.IN_QUEUE;
		}
		if((y != Y_MAX - 1) && (LandMarker.FARMABLE == farmland[x][y+1])){
			coordQueue.add(new Point(x, y + 1));
			farmland[x][y+1] = LandMarker.IN_QUEUE;
		}
	}

	/**
	 * finds the entire farmable area of each item in coordQueue. This function expects there to be coordinates from a single farmable area in coordQueue, not from 2 or more seperate area.
	 * @return - returns area total area of the farmable plot of the points in the Queue.
	 */
	private int findEntireFarmableArea(){
		Point currentPoint;
		int curArea = 0;
		while(!coordQueue.isEmpty()){
			currentPoint = coordQueue.peek();
			enqueueSurroundingCoordinates(currentPoint.x, currentPoint.y);
			curArea++;
			coordQueue.poll();
			farmland[currentPoint.x][currentPoint.y] = LandMarker.FARMABLE_MARKED;
		}
		
		return curArea;
	}
}
