package com.game_machine.core;

/*
 * Implements fast 2d spatial hashing.  Neighbor queries return all entities that are in our cell and neighboring cells.  The bounding is a box not a radius,
 * so there is no way to get all entities within an exact range.  This is normally not an issue for large numbers of entities in a large open space, and if you are
 * working with a small number of entities you can afford to do additional filtering client side.
 * 
 * Grids are instantiated with a size and a cell size.  The grid is divided into cells of cell size.  The cell size must divide evenly into the grid size.
 * 
 * Grids operations are thread safe.
 * 
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import GameMachine.Messages.TrackData;
import GameMachine.Messages.TrackData.EntityType;

public class Grid {

	private int max;
	private int cellSize = 0;
	private float convFactor;
	private int width;
	private int cellCount;

	private ConcurrentHashMap<String, TrackData> deltaIndex = new ConcurrentHashMap<String, TrackData>();
	private ConcurrentHashMap<String, TrackData> objectIndex = new ConcurrentHashMap<String, TrackData>();
	private ConcurrentHashMap<String, Integer> cellsIndex = new ConcurrentHashMap<String, Integer>();
	private ConcurrentHashMap<Integer, ConcurrentHashMap<String, TrackData>> cells = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, TrackData>>();

	public Grid(int max, int cellSize) {
		this.max = max;
		this.cellSize = cellSize;
		this.convFactor = 1.0f / this.cellSize;
		this.width = (int) (this.max / this.cellSize);
		this.cellCount = this.width * this.width;
	}

	public int getObjectCount() {
		return objectIndex.size();
	}

	public int getMax() {
		return this.max;
	}

	public int getCellSize() {
		return this.cellSize;
	}

	public int getWidth() {
		return this.width;
	}

	public int getCellCount() {
		return this.cellCount;
	}

	public Set<Integer> cellsWithinBounds(float x, float y) {
		Set<Integer> cells = new HashSet<Integer>();

		int offset = this.cellSize;

		int startX = (int) (x - offset);
		int startY = (int) (y - offset);

		// subtract one from offset to keep it from hashing to the next cell
		// boundary outside of range
		int endX = (int) (x + offset - 1);
		int endY = (int) (y + offset - 1);

		for (int rowNum = startX; rowNum <= endX; rowNum += offset) {
			for (int colNum = startY; colNum <= endY; colNum += offset) {
				if (rowNum >= 0 && colNum >= 0) {
					cells.add(hash(rowNum, colNum));
				}
			}
		}
		return cells;
	}

	public ArrayList<TrackData> neighbors(float x, float y, EntityType entityType) {
		ArrayList<TrackData> result;

		Collection<TrackData> gridValues;
		result = new ArrayList<TrackData>();
		Set<Integer> cells = cellsWithinBounds(x, y);
		for (int cell : cells) {
			gridValues = gridValuesInCell(cell);
			if (gridValues == null) {
				continue;
			}

			for (TrackData gridValue : gridValues) {
				if (gridValue != null) {
					if (entityType == null) {
						result.add(gridValue);
					} else if (gridValue.entityType == entityType) {
						result.add(gridValue);
					}
				}
			}
		}
		return result;
	}

	public Collection<TrackData> gridValuesInCell(int cell) {
		ConcurrentHashMap<String, TrackData> cellGridValues = cells.get(cell);

		if (cellGridValues != null) {
			return cellGridValues.values();
		} else {
			return null;
		}
	}

	public void updateFromDelta(TrackData[] gridValues) {
		for (TrackData gridValue : gridValues) {
			if (gridValue != null) {
				objectIndex.put(gridValue.id, gridValue);
			}
		}
	}

	public TrackData[] currentDelta() {
		TrackData[] a = new TrackData[deltaIndex.size()];
		deltaIndex.values().toArray(a);
		deltaIndex.clear();
		return a;
	}

	public ArrayList<TrackData> getNeighborsFor(String id, EntityType entityType) {
		TrackData gridValue = get(id);
		if (gridValue == null) {
			return null;
		}
		return neighbors(gridValue.x, gridValue.y, entityType);
	}

	public List<TrackData> getAll() {
		return new ArrayList<TrackData>(objectIndex.values());
	}

	public TrackData get(String id) {
		return objectIndex.get(id);
	}

	public void remove(String id) {
		TrackData indexValue = objectIndex.get(id);
		if (indexValue != null) {
			int cell = cellsIndex.get(id);
			ConcurrentHashMap<String, TrackData> cellGridValues = cells.get(cell);
			if (cellGridValues != null) {
				cellGridValues.remove(id);
			}
			objectIndex.remove(id);
			cellsIndex.remove(id);
		}
	}

	public Boolean set(String id, float x, float y, float z, EntityType entityType) {
		TrackData trackData = new TrackData();
		trackData.id = id;
		trackData.x = x;
		trackData.y = y;
		trackData.z = z;
		trackData.entityType = entityType;
		trackData.setGetNeighbors(0);
		return set(trackData);
	}

	public Boolean set(TrackData trackData) {
		String id = trackData.id;

		Integer oldCellValue = cellsIndex.get(id);

		int cell = hash(trackData.x, trackData.y);

		if (oldCellValue != null) {
			if (oldCellValue != cell) {
				ConcurrentHashMap<String, TrackData> cellGridValues = cells.get(oldCellValue);
				if (cellGridValues != null && cellGridValues.containsKey(id)) {
					cellGridValues.remove(id);
				}
				if (cellGridValues != null && cellGridValues.size() == 0) {
					cells.remove(oldCellValue);
				}

			}
			objectIndex.replace(id, trackData);
			cellsIndex.replace(id, cell);
		} else {
			cellsIndex.put(id, cell);
			objectIndex.put(id, trackData);
		}

		ConcurrentHashMap<String, TrackData> cellGridValues = cells.get(cell);
		if (cellGridValues == null) {
			cellGridValues = new ConcurrentHashMap<String, TrackData>();
			cellGridValues.put(id, trackData);
			cells.put(cell, cellGridValues);
		} else {
			cellGridValues.put(id, trackData);
		}

		// deltaIndex.put(id, gridValue);

		return true;
	}

	public int hash2(float x, float y) {
		return (int) (Math.floor(x / this.cellSize) + Math.floor(y / this.cellSize) * width);
	}

	public int hash(float x, float y) {
		return (int) ((x * this.convFactor)) + (int) ((y * this.convFactor)) * this.width;
	}
}