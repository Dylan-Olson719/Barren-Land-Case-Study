package com.example.farmland;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.enums.ReturnTypes;
import com.farmland.FarmlandApplication;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;

//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class FarmlandApplicationTests {

	FarmlandApplication obj;
	@Test
	public void input_test_1(){
		obj = new FarmlandApplication();
		assertTrue(ReturnTypes.RTN_OK == obj.sanitizeInput("{}"));
	}
}
