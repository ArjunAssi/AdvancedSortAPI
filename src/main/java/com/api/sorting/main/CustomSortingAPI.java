package com.api.sorting.main;

import java.io.File;
import java.util.List;

import com.api.sorting.utils.FileUtility;
import com.api.sorting.utils.SortingUtility;

/**
 * API for advanced sorting functionality
 * 
 * @author leonidas
 * @date 21st April
 */
public class CustomSortingAPI {

	// API call for doing external sort on a bunch of input files
	public void externalSortDirectory(String inputDirectory, String outputFile) {

		try {

			// Get the list of files in this directory
			List<String> filesToMerge = FileUtility.INSTANCE
					.getFileListFromDirectory(inputDirectory);

			System.out.println("LIST OF INPUT FILES TO MERGE :");
			filesToMerge.stream().forEach(System.out::println);

			// Merge them in one file
			String interimInputFile = inputDirectory + File.separator
					+ "MERGED_FILE.txt";
			FileUtility.INSTANCE.mergeMultipleFiles(filesToMerge,
					interimInputFile);

			System.out.println("INTERIM MERGED INPUT FILE IS : "
					+ interimInputFile);

			// Sort in batches and store in temp file
			List<File> tempSortedFlatFiles = SortingUtility.INSTANCE
					.sortInBatch(new File(interimInputFile),
							(a, b) -> a.compareTo(b));
			System.out.println("GENERATED THE TEMPORARY SORTED FILES");

			// Merge the sorted temp files
			SortingUtility.INSTANCE.mergeSortedFiles(tempSortedFlatFiles,
					new File(outputFile), (a, b) -> a.compareTo(b));

			System.out.println("GENERATED HE MERGED SORTED FILE : "
					+ outputFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// API call for rotating a sorted list
	public List<Integer> rotateSortedList(List<Integer> sortedInputList) {

		return SortingUtility.INSTANCE.rotateSortedList(sortedInputList);
	}

	// API call for rotating a sorted list by a defined rotation
	public List<Integer> rotateSortedList(List<Integer> sortedInputList,
			int rotateCounter) {

		return SortingUtility.INSTANCE.rotateSortedList(sortedInputList,
				rotateCounter);
	}

	// API call for returning the minimum value in a sorted rotated list
	public int minValInSortedRotatedList(List<Integer> sortedRotatedInputList) {
		return SortingUtility.INSTANCE
				.findMinimumElement(sortedRotatedInputList);
	}
}
