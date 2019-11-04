package lists;

import java.util.NoSuchElementException;

public class DoubleLinkedList<T> implements Stack<T>, Queue<T> {
	
	static class Node<T> {
		T data;
		Node<T> prev;
		Node<T> next;
		
		Node(T data) {
			this.data = data;
			prev = this;
			next = this;
		}
		
		// combine this list with another, such that the elements of the other
		// list come between this and this.prev
		void merge(Node<T> other) {
			Node<T> this_back = prev;
			Node<T> other_back = other.prev;

			this_back.next = other;
			other.prev = this_back;

			prev = other_back;
			other_back.next = this;
		}
		
		// unlink this node from its prev/next nodes
		void detach() {
			next.prev = prev;
			prev.next = next;
		}
	}
	
	Node<T> front;
	
	public void addFront(T item) {
		Node<T> new_node = new Node<T>(item);
		if (front != null) {
			front.merge(new_node);
		}
		front = new_node;
	}
	
	public void addBack(T item) {
		Node<T> new_node = new Node<T>(item);
		if (front == null) {
			front = new_node;
		} else {
			front.merge(new_node);
		}
	}
	
	public void append(DoubleLinkedList<T> other) {
		if (other.front == null) {
			return;
		}
		if (front == null) {
			front = other.front;
		}
		front.merge(other.front);
		other.front = null;
	}
	
	public T deleteFront() {
		if (front == null) throw new NoSuchElementException("empty list");
		
		T item = front.data;
		
		if (front.next == front) {
			front = null;
		} else {
			front.detach();
			front = front.next;
		}
		
		return item;
	}
	
	public T deleteBack() {
		if (front == null) throw new NoSuchElementException("empty list");
		
		T item = front.prev.data;
		
		if (front.next == front) {
			front = null;
		} else {
			front.prev.detach();
		}
		
		return item;
	}
	
	public T delete(T item) {
		if (front == null) throw new NoSuchElementException("empty list");
		
		Node<T> crnt = front;

		do {
			if (crnt.data.equals(item)) {
				if (crnt.next == crnt) {
					front = null;

				} else {
					crnt.detach();
				
					if (crnt == front) {
						front = crnt.next;
					}
				}

				return crnt.data;
			}
			
			crnt = crnt.next;
		} while (crnt != front);
		
		throw new NoSuchElementException("item not found: " + item);
	}
	
//	public void addFront(T item) {
//		Node<T> new_node = new Node<T>(item);
//		
//		if (front == null) {
//			front = new_node;
//		} else {
//			new_node.next = front;
//			new_node.prev = front.prev;
//			front.prev.next = new_node;
//			front.prev = new_node;
//			front = new_node;			
//		}
//	}
//
//	public void addBack(T item) {
//		Node<T> new_node = new Node<T>(item);
//		
//		if (front == null) {
//			front = new_node;
//		} else {
//			new_node.next = front;
//			new_node.prev = front.prev;
//			front.prev.next = new_node;
//			front.prev = new_node;
//		}
//	}
//	
//	public void deleteFront() {
//		if (front == null) {
//			throw new NoSuchElementException("empty list");
//		}
//		
//		if (front.next == front) {
//			front = null;
//			return;
//		}
//		
//		Node<T> new_front = front.next;
//		Node<T> back = front.prev;
//		back.next = new_front;
//		new_front.prev = back;
//		front = new_front;
//	}
//	
//	public void deleteBack() {
//		if (front == null) {
//			throw new NoSuchElementException("empty list");
//		}
//		
//		if (front.next == front) {
//			front = null;
//			return;
//		}
//		
//		Node<T> new_back = front.prev.prev;
//		new_back.next = front;
//		front.prev = new_back;
//	}
	
	public void traverse() {
		if (front == null) return;
		
		Node<T> crnt = front;
		do {
			System.out.println(crnt.data);
			crnt = crnt.next;
		} while (crnt != front);
	}
	
	public boolean isEmpty() {
		return front == null;
	}
	
	// queue methods add to back and remove from front
	// the opposite choice would work just as well
	public void enqueue(T item) {
		addBack(item);
	}
	
	public T dequeue() {
		return deleteFront();
	}
	
	// stack methods add to front and remove from front
	public void push(T item) {
		addFront(item);
	}
	
	public T pop() {
		return deleteFront();
	}
	
	public T peek() {
		if (front == null)
			throw new NoSuchElementException("empty list");
		
		return front.data;
	}
	

	public static void main(String[] args) {
		DoubleLinkedList<String> L = new DoubleLinkedList<String>();
		L.addBack("hello");
		L.addBack("world");
		L.addBack("!");
		L.traverse();
	}
	
	
}
