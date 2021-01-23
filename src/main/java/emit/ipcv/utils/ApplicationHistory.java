package emit.ipcv.utils;

import java.util.ArrayList;
import java.util.List;

public class ApplicationHistory<E> {
	private List<E> elements;
	private int cursor;
	private boolean backHistory;
	
	public ApplicationHistory() {
		elements = new ArrayList<>();
		cursor = -1;
		backHistory = false;
	}
	
	public E undo() {
		backHistory = true;
		return this.elements.get(--cursor);
	}
	
	public E redo() {
		return this.elements.get(++cursor);
	}
	
	public ApplicationHistory<E> append(E element) {
		if (isBackHistory()) {
			for (int i = elements.size() - 1; i > cursor; i--) {
				elements.remove(i);
			}
			backHistory = false;
		}
		this.elements.add(++cursor, element);
		return this;
	}
	
	private boolean isBackHistory() {
		return backHistory;
	}
	
	public boolean hasPreviousElement() {
		return cursor > 0;
	}
	
	public boolean hasMoreElement(){
		return cursor < elements.size() - 1;
	}
}
