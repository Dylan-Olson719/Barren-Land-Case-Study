# Barren Land Case Study

## Overview
This program is designed to tell you the size of each farmable area in a field with a mix of farmable and barren land. The field is 400 units by 600 units. The barren land is always in rectangles. The farmable land has no specific shape.

The program will ask the user for a set of rectangles of barren land to process.
These rectangles must be of the form...

- "10 20 30 40"

Where the first pair of integers represent the X and Y coodinates of the lower left corner, and the 2nd pair represent the top right corner. The X coordinates must be between 0 and 399, and the Y coordinates must be between 0 and 599.
The set of rectangles provided must start with an opening bracket { and end with a closing bracket }. There must also be a comma between each of the rectangles provided.

**Examples of valid sets of rectangles**

- {}
- {"10 20 30 40"}
- {"48 192 351 207", "48 392 351 407", "120 52 135 547", "260 52 275 547"}

**NOTE: an empty set is still a valid set in this implementation**
Additional sets of rectangles can be found in farmland/Example sets of rectangles.txt

**Examples of invalid sets of rectangles:**

- {"a b c d"}
- {"1.1 2.2 3.3 4.4"}
- {"1 2 3"}
- {"-1 292 399 307"}

The program will output a list of areas of all of the different farmable area sections, from smallest to greatest.

## Requirements:

- Java 17. This has not yet been tested on Java 8 or Java 11.

**To run the program, do the following:**
install java 17
install VScode and the "Extension Pack for Java" plugin for VScode
hit the run button on FarmlandApplication.java

TODO: determine how to fully run this from CMD
run `java -cp .\farmland\target\classes\ com.farmland.FarmlandApplication` from the root project directory

## Testing:

For testing, open the project in VS code and use the testing tools to run the JUnit tests in FarmlandApplicationTests.java