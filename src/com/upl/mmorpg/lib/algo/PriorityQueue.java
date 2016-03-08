package com.upl.mmorpg.lib.algo;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public final class PriorityQueue <E>
{
	public PriorityQueue(boolean autosort)
	{
		nodes = new LinkedList<PriorityNode<E>>();
		this.autosort = autosort;
	}
	
	public boolean empty()
	{
		return nodes.isEmpty();
	}
	
	public void enqueue(double priority, E obj)
	{
		nodes.add(new PriorityNode<E>(priority, obj));
		
		if(autosort) this.sort();
	}
	
	public double peekHighestPriority()
	{
		if(nodes.size() == 0) return -1;
		return nodes.getLast().priority;
	}
	
	public double peekLowestPriority()
	{
		if(nodes.size() == 0) return -1;
		return nodes.getFirst().priority;
	}
	
	public E dequeueHighestPriority()
	{
		PriorityNode<E> node = nodes.removeLast();
		return node.obj;
	}
	
	public E dequeueLowestPriority()
	{
		PriorityNode<E> node = nodes.removeFirst();
		return node.obj;
	}
	
	public void sort()
	{
		Collections.sort(nodes);
	}
	
	public void dumpQueue()
	{
		System.out.println("QUEUE:");
		Iterator<PriorityNode<E>> it = nodes.iterator();
		while(it.hasNext())
			System.out.println(it.next().priority + "");
		System.out.println("\n");
	}
	
	private LinkedList<PriorityNode<E>> nodes;
	private boolean autosort;
	
	private class PriorityNode <Q> implements Comparable<PriorityNode<Q>>
	{
		public PriorityNode(double priority, Q obj)
		{
			this.priority = priority;
			this.obj = obj;
		}
		
		@Override
		public int compareTo(PriorityNode<Q> node) 
		{
			if(node.priority == priority)
				return 0;
			else if(priority > node.priority)
				return 1;
			return -1;
		}
		
		private double priority;
		private Q obj;
	}
}
