package com.example.demo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Test {
	public static void main(String[] a) {
		try {
			int x = 7;
			assert (x == 6);
			System.out.println(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
