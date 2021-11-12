# Barren Land Case Study
This is a project to determine the sizes of all distinct and seperate plots of farmland in a plot of 400 units long by 600 units wide.
The program will ask the user for rectangle of barren land to process.
These rectangles must be of the form...

- "10 20 30 40"

Where the first pair of integers represent the X and Y coodinates of the lower left corner, and the 2nd pair represent the top right corner. The X coordinate must be between 0 and 399, where as the Y coordinate must be between 0 and 599.
The set of rectangles provided must start with an opening bracket { and end with a closing bracket }. There must also be a comma between each of the rectangles provided.

**Examples of valid sets of rectangles**

- {}
- {"10 20 30 40"}
- {"48 192 351 207", "48 392 351 407", "120 52 135 547", "260 52 275 547"}

**NOTE: an empty set is still a valid set**

**Examples of invalid sets of rectangles:**

- {"a b c d"}
- {"1.1 2.2 3.3 4.4"}
- {"1 2 3"}
- {"-1 292 399 307"}

## Requirements:

I developed this on Java 17. I have not yet tested this on Java 8 or Java 11.

**To run the program, do the following:**
install java 17
install VScode and the "Extension Pack for Java" plugin for VScode
hit the run button on FarmlandApplication.java

TODO: determine how to fully run this from CMD
run `java -cp .\farmland\target\classes\ com.farmland.FarmlandApplication` from the root project directory

## Testing:

For testing, I opened the project in VS code and used the testing tools to run the JUnit tests I made in FarmlandApplicationTests.java