package com.api.sorting.utils;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This enum deals with the providing access to File related operations
 * 
 * @author leonidas
 * @date 21st April
 */
public enum FileUtility {

	// Instance to get access to functions
	INSTANCE;
	// Max temporary files to be generated
	final static int MAX_TEMP_FILES = 500;

	// Function to merge the contents of multiple files into one file
	public void mergeMultipleFiles(List<String> filesToMerge, String outputFile)
			throws IOException {

		// Get the path to output file
		Path outFile = Paths.get(outputFile);

		// Get the outputFile channel
		FileChannel outputFileChannel = FileChannel
				.open(outFile, EnumSet.of(StandardOpenOption.CREATE,
						StandardOpenOption.WRITE));

		// Iterate over the list of files to merge and get their coresponding
		// channels
		for (String inputFile : filesToMerge) {

			// Get the path to the input file
			Path inFile = Paths.get(inputFile);

			// Get the channel from input files
			FileChannel inputFileChannel = FileChannel.open(inFile,
					EnumSet.of(StandardOpenOption.CREATE));

			// Write the data to the outputfile
			for (long index = 0, length = inputFileChannel.size(); index < length;)
				index = index
						+ inputFileChannel.transferTo(index, length - index,
								outputFileChannel);
		}

	}

	// Function to get the list of files from a directory
	public List<String> getFileListFromDirectory(String directoryPath)
			throws Exception {

		// Get the directory object for the path
		File inputDirectory = new File(directoryPath);

		// Check if the input directory exists
		if (!inputDirectory.exists()) {
			throw new Exception("The input directory does not exist");
		} else {
			// convert it to list and send
			return Files.list(Paths.get(directoryPath))
					.filter(Files::isRegularFile).map(Path::toString)
					.collect(Collectors.toList());
		}
	}

	// Function to get the best size of block from file to be sorted
	public long generateBlockSize(File inputFile) {

		// Initial block size
		long blockSize = inputFile.length() / MAX_TEMP_FILES;

		// Get the free memory
		long freeMemory = Runtime.getRuntime().freeMemory();

		// Get the best block size by this logic
		if (blockSize < freeMemory / 2) {
			blockSize = freeMemory / 2;
		} else {
			if (blockSize >= freeMemory) {
				System.err.println("Memory limit will reached");
			}
		}
		// Return the block size
		return blockSize;
	}


}
