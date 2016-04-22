package com.api.sorting.dto;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is a wrapper on top of BufferedReader to keep last read line in memory
 * 
 * @author leonidas
 * @date 21st April
 */
public class CustomFileBuffer {

	public static int READ_BUFFER_SIZE = 2048;
	public BufferedReader bufferedReader;
	public File originalfile;
	private String imMemory;
	private boolean empty;

	public CustomFileBuffer(File file) throws IOException {
		originalfile = file;
		bufferedReader = new BufferedReader(new FileReader(file), READ_BUFFER_SIZE);
		reload();
	}

	public boolean empty() {
		return empty;
	}

	private void reload() throws IOException {
		try {
			if ((this.imMemory = bufferedReader.readLine()) == null) {
				empty = true;
				imMemory = null;
			} else {
				empty = false;
			}
		} catch (EOFException eofException) {
			empty = true;
			imMemory = null;
		}
	}

	public void close() throws IOException {
		bufferedReader.close();
	}

	public String peek() {
		if (empty())
			return null;
		return imMemory.toString();
	}

	public String pop() throws IOException {
		String answer = peek();
		reload();
		return answer;
	}
}
