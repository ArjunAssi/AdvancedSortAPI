package com.api.sorting.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

import com.api.sorting.dto.CustomFileBuffer;

/**
 * This enum deals with the providing sorting utilities
 * 
 * @author leonidas
 * @date 21st April
 */
public enum SortingUtility {

	// Instance to get access to functions
	INSTANCE;

	// This function will sort a block of data form main file and then write to
	// temporary file on disk
	public List<File> sortInBatch(File inputFile, Comparator<String> comparator)
			throws IOException {

		// Initialize the list of sorted files to be generated
		List<File> sortedTempFiles = new ArrayList<File>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				inputFile));
		List<String> dataListForTempFile = new ArrayList<String>();
		String inputLine = "";

		// Block Size for the input file
		long blockSize = FileUtility.INSTANCE.generateBlockSize(inputFile);

		try {
			// Read while the input line is not null
			while (inputLine != null) {

				// Initialize block size for current file as 0
				long currentBlockSize = 0;
				while ((currentBlockSize < blockSize)
						&& ((inputLine = bufferedReader.readLine()) != null)) {
					// Add the line for temp list
					dataListForTempFile.add(inputLine);
					currentBlockSize += inputLine.length() * 2 + 100;
				}
				// Add to soreted temp files
				sortedTempFiles.add(sortAndSaveTempFile(dataListForTempFile,
						comparator));
				dataListForTempFile.clear();
			}
		} catch (EOFException oef) {
			if (dataListForTempFile.size() > 0) {
				sortedTempFiles.add(sortAndSaveTempFile(dataListForTempFile,
						comparator));
				dataListForTempFile.clear();
			}
		} finally {
			bufferedReader.close();
		}
		// return the sorted list of temp files
		return sortedTempFiles;
	}

	// Function to sort and save temporary files
	public File sortAndSaveTempFile(List<String> tempList,
			Comparator<String> comparator) throws IOException {

		// Sort the temp list
		Collections.sort(tempList, comparator);
		File temporaryFile = File.createTempFile("batchSort", "tempFile");
		temporaryFile.deleteOnExit();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				temporaryFile));
		try {
			// Iterate over the list of string and write to disk
			for (String tempString : tempList) {
				bufferedWriter.write(tempString);
				bufferedWriter.newLine();
			}
		} finally {
			bufferedWriter.close();
		}
		// Return the temporary file
		return temporaryFile;
	}

	// This function is to merge the sorted files
	public static int mergeSortedFiles(List<File> files, File outputfile,
			final Comparator<String> comparator) throws IOException {

		PriorityQueue<CustomFileBuffer> priorityQueue = new PriorityQueue<CustomFileBuffer>(
				20, (CustomFileBuffer i, CustomFileBuffer j) -> i.peek()
						.compareTo(j.peek()));

		// Iterate through the files and add to custome buffer
		for (File file : files) {
			CustomFileBuffer customFileBuffer = new CustomFileBuffer(file);
			priorityQueue.add(customFileBuffer);
		}

		// Get a writer to output file
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				outputfile));
		int rowcounter = 0;

		try {
			while (priorityQueue.size() > 0) {
				CustomFileBuffer buffer = priorityQueue.poll();
				String line = buffer.pop();
				bufferedWriter.write(line);
				bufferedWriter.newLine();
				++rowcounter;
				if (buffer.empty()) {
					buffer.bufferedReader.close();
					buffer.originalfile.delete();
				} else {
					priorityQueue.add(buffer); // add it back
				}
			}
		} finally {
			bufferedWriter.close();

			// Close all buffered readers
			for (CustomFileBuffer buffer : priorityQueue)
				buffer.close();
		}
		return rowcounter;
	}

	// This function is to rotate a sorted list by a given counter
	public List<Integer> rotateSortedList(List<Integer> sortedList,
			int rotationCount) {

		// Check if the parameters are correct
		if (null == sortedList || rotationCount < 0) {
			throw new IllegalArgumentException("Illegal argument!");
		}

		// Use the bubble rotate method
		for (int i = 0; i < rotationCount; i++) {
			for (int j = sortedList.size() - 1; j > 0; j--) {
				int temp = sortedList.get(j);
				sortedList.set(j, sortedList.get(j - 1));
				sortedList.set(j - 1, temp);
			}
		}
		return sortedList;
	}

	// This function is to rotate a sorted list by a random counter
	public List<Integer> rotateSortedList(List<Integer> sortedList) {

		// Check if the parameters are correct
		if (null == sortedList) {
			throw new IllegalArgumentException("Illegal argument!");
		}

		int rotationCount = ThreadLocalRandom.current().nextInt(0,
				sortedList.size() + 1);

		// Use the bubble rotate method
		for (int i = 0; i < rotationCount; i++) {
			for (int j = sortedList.size() - 1; j > 0; j--) {
				int temp = sortedList.get(j);
				sortedList.set(j, sortedList.get(j - 1));
				sortedList.set(j - 1, temp);
			}
		}
		return sortedList;
	}

	// This function is to identify the minimum element of a sorted rotated list
	public int findMinimumElement(List<Integer> sortedRotatedList) {

		return findMinimumElement(sortedRotatedList, 0,
				sortedRotatedList.size() - 1);
	}

	// This function is to identify the minimum element of a sorted rotated list
	public int findMinimumElement(List<Integer> sortedRotatedList,
			int startIndex, int endIndex) {

		// If only once element exist
		if (endIndex == startIndex) {
			return sortedRotatedList.get(startIndex);
		}
		if (endIndex == startIndex + 1) {
			return Math.min(sortedRotatedList.get(startIndex),
					sortedRotatedList.get(endIndex));
		}

		int middle = (endIndex - startIndex) / 2 + startIndex;
		// already sorted
		if (sortedRotatedList.get(endIndex) > sortedRotatedList.get(startIndex)) {
			return sortedRotatedList.get(startIndex);
			// right shift one
		} else if (sortedRotatedList.get(endIndex) == sortedRotatedList
				.get(startIndex)) {
			return findMinimumElement(sortedRotatedList, startIndex + 1,
					endIndex);
			// go right
		} else if (sortedRotatedList.get(middle) >= sortedRotatedList
				.get(startIndex)) {
			return findMinimumElement(sortedRotatedList, middle, endIndex);
			// go left
		} else {
			return findMinimumElement(sortedRotatedList, startIndex, middle);
		}
	}

}
