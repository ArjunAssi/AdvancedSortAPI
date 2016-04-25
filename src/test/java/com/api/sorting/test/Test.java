package com.api.sorting.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.api.sorting.main.CustomSortingAPI;
import com.api.sorting.utils.SortingUtility;

/**
 * Manual Test class for API testing
 * 
 * @author leonidas
 * @date 21st April
 */
public class Test {

	public static void main(String[] args) throws Exception {

		// INITIALIZE THE TEST PARAMETERS
		String inputDirectory = "/home/leonidas/Desktop/TestFolder";
		String outputFile = "sortedFile.txt";
		List<Integer> sortedList = new ArrayList<Integer>();
		sortedList.add(-1);
		sortedList.add(2);
		sortedList.add(3);
		sortedList.add(4);
		sortedList.add(5);
		sortedList.add(6);
		sortedList.add(7);
		sortedList.add(8);

		// INSTANTIATE THE API OBJECT
		CustomSortingAPI customSortingAPI = new CustomSortingAPI();

		// TESTING EXTERNAL SORT
		customSortingAPI.externalSortDirectory(inputDirectory, inputDirectory
				+ File.separator + outputFile);

		// TESTING SORTED LIST ROTATION WITHOUT ROTATION COUNTER
		List<Integer> rotatedList = SortingUtility.INSTANCE
				.rotateSortedList(sortedList);
		rotatedList.stream().forEach(System.out::println);

		// TESTING SORTED LIST ROTATION WITH ROTATION COUNTER
		List<Integer> rotatedListWithCount = SortingUtility.INSTANCE
				.rotateSortedList(sortedList, 4);
		rotatedListWithCount.stream().forEach(System.out::println);

		// TESTING FOR MINIMUM ELEMENT IN SORTED ROTATED LIST
		int minElement = SortingUtility.INSTANCE
				.findMinimumElement(rotatedList);
		System.out.println("Min element is " + minElement);
	}
}
