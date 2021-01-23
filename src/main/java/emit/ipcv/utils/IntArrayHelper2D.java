/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.utils;

/**
 * @author rinelfi
 */
public class IntArrayHelper2D {
	
	private int[][] array;
	
	public IntArrayHelper2D(int[][] array) {
		this.array = array;
	}
	
	public int lineLength() {
		return array.length;
	}
	
	public int columnLength() {
		return array.length > 0 ? array[0].length : 0;
	}
	
	public int[][] slice(int xMin, int yMin, int xMax, int yMax) {
		final int xLength = xMax - xMin + 1, yLength = yMax - yMin + 1;
		int[][] slice = new int[xLength][yLength];
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				slice[x][y] = array[xMin + x][yMin + y];
			}
		}
		return slice;
	}
	
	public int get(int x, int y) {
		return this.array[x][y];
	}
	
	public int getMeidan() {
		int indexPopulation = 0;
		int[] populations = new int[lineLength() * columnLength()];
		for (int x = 0; x < lineLength(); x++) {
			for (int y = 0; y < columnLength(); y++) {
				populations[indexPopulation++] = this.array[x][y];
			}
		}
		// triage des valeurs
		boolean trie = true;
		while (trie) {
			trie = false;
			for (int i = 1; i < populations.length; i++) {
				if (populations[i] < populations[i - 1]) {
					trie = true;
					int population = populations[i];
					populations[i] = populations[i - 1];
					populations[i - 1] = population;
				}
			}
		}
		return (int) populations[(populations.length - 1) / 2];
	}
	
	public float getAverage() {
		int populationSum = 0, total = 0;
		for (int line = 0; line < lineLength(); line++) {
			for (int column = 0; column < columnLength(); column++) {
				populationSum += array[line][column];
				total++;
			}
		}
		return (float) populationSum / (float) total;
	}
	
	public int getConservativeValue() {
		final int target = array[(lineLength() - 1) / 2][(columnLength() - 1) / 2];
		int max = array[0][0], min = array[0][0];
		for (int line = 0; line < lineLength(); line++) {
			for (int column = 0; column < columnLength(); column++) {
				if (line == column && line == ((lineLength() - 1) / 2)) {
					break;
				}
				max = array[line][column] > max ? array[line][column] : max;
				min = array[line][column] < min ? array[line][column] : min;
			}
		}
		return target > max || target < min ? (target > max ? max : min) : target;
	}
	
}
