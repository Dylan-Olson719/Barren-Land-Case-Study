package com.farmland;

import java.io.Console;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.barrenland.BarrenLand;
import com.enums.*;
import com.input_sanitization.InputSanitization;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;

//@SpringBootApplication
public class FarmlandApplication{

	//@Autowired
	private InputSanitization rawInput = new InputSanitization();
	private LandMarker[][] Farmland = new LandMarker[400][600];
	private List<BarrenLand> barrenLandList = new LinkedList<BarrenLand>();
	static Console console;

	public static void main(String[] args) {
		//SpringApplication.run(FarmlandApplication.class, args);
		FarmlandApplication instance = new FarmlandApplication();
		
		console = System.console();
		instance.initializeFarmland();
		ReturnTypes retCode = ReturnTypes.RTN_ERROR;
		String input;
		do{
			input = console.readLine("Please enter the set of rectangles for the barren land\n"
			+ "Example input: {\"10 20 30 40\", \"110 120 130 140\"}");
			retCode = instance.sanitizeInput(input);
			if (ReturnTypes.RTN_OK == retCode){
				instance.grabRectangles(input);
			}
		}while(ReturnTypes.RTN_ERROR == retCode);

		instance.drawBarrenLandRectangles();
		//Start at first rectangle, check for farmable land around the rectangle
		//after checking, see if we have at least one more rectangle, if so, repeat for that
		//after all rectangles, print out results

		System.out.println("fin");
	}

	private void initializeFarmland(){
		for(int x = 0; x <= 400; x++){
			for(int y = 0; y <= 600; y++){
				Farmland[x][y] = LandMarker.Farmable;
			}
		}
	}

	/** Sanitizes input
	 * example of proper input
	 * {“48 192 351 207”, “48 392 351 407”, “120 52 135 547”, “260 52 275 547”}
	 * @param input - raw input
	 * @return - returns whether or not the input is valid or not
	 */
	private ReturnTypes sanitizeInput(String input){
		//example of proper input
		//  {“48 192 351 207”, “48 392 351 407”, “120 52 135 547”, “260 52 275 547”} 
		int curIndex;
		String temp;
		if ((!input.substring(0, 1).equals("}")) || (input.substring(input.length() - 1).equals("}"))){
			console.printf("Your input is missing either a opening bracket at the start, or a closing bracket at the end.\n");
			return ReturnTypes.RTN_ERROR;
		}
		
		if((-1 != input.substring(1).indexOf("{")) || (input.length() - 1 != input.indexOf("}"))){
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
			num = rectangle.substring(curIndex, rectangle.indexOf(" "));
			if(ReturnTypes.RTN_OK != verifyInteger(num)){
				return ReturnTypes.RTN_ERROR;
			}
			curIndex = rectangle.indexOf(" ") + 1;
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

	private ReturnTypes grabRectangles(String input){
		int x1, y1, x2, y2;
		int curIndex = 1;
		int quote1Loc, quote2Loc;
		String rectangle;

		quote1Loc = input.indexOf("\"", curIndex);
		quote2Loc = input.indexOf("\"", quote1Loc + 1);
		
		if(-1 == quote1Loc){
			//we dont have any rectangles
			return ReturnTypes.RTN_OK;
		}

		rectangle = input.substring(quote1Loc + 1, quote2Loc);

		x1 = Integer.parseInt(input.substring(curIndex, rectangle.indexOf(" ")));
		curIndex = rectangle.indexOf(" ") + 1;
		y1 = Integer.parseInt(input.substring(curIndex, rectangle.indexOf(" ")));
		curIndex = rectangle.indexOf(" ") + 1;
		x2 = Integer.parseInt(input.substring(curIndex, rectangle.indexOf(" ")));
		curIndex = rectangle.indexOf(" ") + 1;
		y2 = Integer.parseInt(input.substring(curIndex));

		//checking to make sure our coordinates are withing the bounds of our farmable area
		if((400 <= x1) || (0 > x1) || (400 <= x2) || (0 > x2) || (x1 > x2) ||
			(600 <= y1) || (0 > y1) || (600 <= y2) || (0 > y2) || (y1 > y2)){
				console.printf("ERROR: Rectangle goes out of bounds or . Please double check the input and try again\n");
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
		return ReturnTypes.RTN_OK;
	}

	private void drawBarrenLandRectangles(){
		Iterator<BarrenLand> itr = barrenLandList.iterator();
		while(itr.hasNext()){
			drawBarrenLandRectangle(itr.next());
		}
	}

	private void drawBarrenLandRectangle(BarrenLand barren){
		for(int x = barren.GetX1(); x <= barren.GetX2(); x++){
			for(int y = barren.GetY1(); y <= barren.GetY2(); y++){
				Farmland[x][y] = LandMarker.Barren;
			}
		}
	}
}
