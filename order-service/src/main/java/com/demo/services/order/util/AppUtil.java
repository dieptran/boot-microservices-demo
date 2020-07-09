package com.demo.services.order.util;

import java.util.Random;

public class AppUtil {

	public static final int RANDOM_MIN = 1000;
	public static final int RANDOM_MAX = 10000000;

	public static int getRandomNumber() {
		return getRandomNumberInRange(RANDOM_MIN, RANDOM_MAX);
	}

	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
