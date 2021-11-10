package com.input_sanitization;

import java.io.Console;
//import org.springframework.stereotype.Service;

import com.enums.ReturnTypes;

//@Service
public class InputSanitization {

    private Console console;

    public InputSanitization(){
        console = System.console();
    }

    public static ReturnTypes ReadInRawInput(String userInput){
        
        // do we start and end in a bracket
        // find the start and end of a rectangle
        //       verify we find the second quote before a comma
        //once found, verify 4 integers inside of there
        //move onto next
        //repeat until we hit the end

        

        return ReturnTypes.RTN_OK;
    }
}