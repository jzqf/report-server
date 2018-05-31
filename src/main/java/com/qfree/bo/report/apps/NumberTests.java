package com.qfree.bo.report.apps;

public class NumberTests {

	public static void main(String[] args) {

		Integer integer1 = 1;
		Integer integer2 = 2;

		Double double1 = 1.0;
		Double double2 = 2.0;

		Object integerObject1 = 1;
		Object integerObject2 = 2;

		Object doubleObject1 = 1.0;
		Object doubleObject2 = 2.0;

		if (integer2 > integer1) {
			System.out.println("integer2>integer1");
		}

		if (double2 > double1) {
			System.out.println("double2>double1");
		}

		if ((Integer) integerObject2 > (Integer) integerObject1) {

		}

		int int1 = 1;
		if (Long.class.isAssignableFrom(int.class)) {
			System.out.println("Long.class.isAssignableFrom(int.class");
		}

	}

}
