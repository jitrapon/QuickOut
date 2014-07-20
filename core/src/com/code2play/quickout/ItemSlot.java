package com.code2play.quickout;
/**
 * Class to handle storing Item (implements Collectible) objects in-game
 * Replace Object type with Item type
 * @author Jitrapon Tiachunpun
 *
 */
public class ItemSlot {

//	public static void main(String[] args) {
//		ItemSlot itemSlot = new ItemSlot(3);
//		itemSlot.addItem("DoubleScore");
//		itemSlot.removeItem(0);
//		itemSlot.addItem("Apple");
//		itemSlot.addItem("Banana");
//		itemSlot.addItem("Orange");
//		itemSlot.removeItem(0);
//		itemSlot.removeItem(2);
//		itemSlot.addItem("Mango");
//		
//		System.out.println(itemSlot);
//		System.out.println("Current fill index: " + itemSlot.getNextFillSlotIndex());
//	}

	private Object[] slots;									// array to hold items
	private int maxSize;									// maximum item slots available at this Level instance
	private boolean isLocked;								// indicates whether player can access or modify the slots
	private int fillIndex;									// the index of the slot to be filled for the next item stored
	private int size;										// the number of items in the slots at the moment

	/**
	 * Construct an initially empty item slot with the maximum size specified
	 * @param size The maximum items allowed
	 */
	public ItemSlot(int size) {
		maxSize = size;
		isLocked = false;
		fillIndex = 0;
		slots = new Object[maxSize];
		size = 0;
	}
	
	public int getSize() {
		return slots.length;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.maxSize; i++) {
			sb.append(i+1 + ". " + slots[i] + "\n");
		}
		return sb.toString();
	}

	public int getNextFillSlotIndex() {
		return fillIndex;
	}

	/**
	 * Add item to the slot.
	 * The specified item will be added to the next empty slot.
	 * If the slot is full, it will be added to the slot pointed by the current
	 * fill index (items will NOT be added to the same slot two consecutive times).
	 * @param item
	 */
	public void addItem(Object item) {
		if (!isLocked) {
			slots[fillIndex] = item;
			size++;
			size = size > maxSize ? maxSize : size;

			// if full, the next item will replace the one stored in the next slot
			if (this.isFull()) {
				incrementIndex();
			}

			// if not full, traverse the index to the next empty slot
			else {
				do {
					incrementIndex();
				}
				while (slots[fillIndex] != null);
			}
		}
	}

	/**
	 * Remove item from the slot.
	 * The occupied slot will be free for the next item to be stored.
	 * @param index	The index of item to be removed
	 */
	public Object removeItem(int index) {
		if (!isLocked) {
			if (index > maxSize-1 || index < 0) return null; 
			if (slots[index] == null) return null;
			Object item = slots[index];
			slots[index] = null;
			size--;

			// find the first empty slot starting from index 0 
			// if there's no empty slot, the current removed item slot will be the index
			for (int i = 0; i < fillIndex; i++) {
				if (slots[i] == null) fillIndex = i;
			}
			return item;
		}
		return null;
	}

	private void incrementIndex() {
		if (fillIndex == maxSize-1) 
			fillIndex = 0;
		else 
			fillIndex++;
	}

	/**
	 * Determines whether the slots are full. By default, full slots can still 
	 * store items, but they will replace the existing ones.
	 * @return
	 */
	public boolean isFull() {
		return size >= maxSize;
	}


}
