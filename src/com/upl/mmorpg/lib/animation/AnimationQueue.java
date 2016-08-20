package com.upl.mmorpg.lib.animation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.lib.liblog.Log;

public class AnimationQueue implements Collection<Animation>
{
	public AnimationQueue(AnimationManager manager, Animation defaultAnimation)
	{
		this.manager = manager;
		this.defaultAnimation = defaultAnimation;
		queue = new LinkedList<Animation>();
		queue.add(defaultAnimation);
	}
	
	public Animation getCurrent()
	{
		if(queue.isEmpty())
			checkDefault();
		return queue.getFirst();
	}
	
	/**
	 * Make sure at least one animation is in the queue. If not, add
	 * and start the default animation.
	 */
	private synchronized void checkDefault()
	{
		if(queue.isEmpty())
		{
			queue.add(defaultAnimation);
			manager.animationChanged();
			defaultAnimation.animationStarted();
		}
	}
	
	/**
	 * Transition to the given animation. The animation will not start
	 * right away, the current animation will be given a chance to
	 * finish.
	 * @param animation The new animation to play.
	 */
	public synchronized void transitionTo(Animation animation)
	{
		queue.clear();
		checkDefault();
		queue.add(animation);
		queue.getFirst().interrupt();
	}
	
	/**
	 * Move to the next animation.
	 */
	public void nextAnimation()
	{
		queue.removeFirst();
		checkDefault();
		manager.animationChanged();
		queue.getFirst().animationStarted();
	}
	
	@Override
	public synchronized boolean add(Animation animation) 
	{
		return queue.add(animation);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Animation> c) 
	{
		return queue.addAll(c);
	}

	@Override
	public synchronized void clear() 
	{
		queue.clear();
		queue.add(defaultAnimation);
		defaultAnimation.animationStarted();
	}

	@Override
	public synchronized boolean contains(Object o) 
	{
		return queue.contains(o);
	}

	@Override
	public synchronized boolean remove(Object o) 
	{
		boolean result = queue.remove(o);
		checkDefault();
		return result;
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) 
	{
		boolean result = queue.removeAll(c);
		checkDefault();
		return result;
	}
	
	@Override 
	public synchronized boolean retainAll(Collection<?> c) 
	{ 
		boolean result = queue.retainAll(c);
		checkDefault();
		return result;
	}
	
	@Override 
	public synchronized boolean isEmpty() 
	{ 
		if(queue.isEmpty())
			Log.vln("Invalid state for animation queue!!");
		return queue.isEmpty(); 
	}
	
	@Override public synchronized int size() { return queue.size(); }
	@Override public synchronized Object[] toArray() { return queue.toArray(); }
	@Override public synchronized <T> T[] toArray(T[] a) { return queue.toArray(a); }
	@Override public synchronized boolean containsAll(Collection<?> c) { return queue.containsAll(c); }
	
	@Override public synchronized Iterator<Animation> iterator() { return queue.iterator(); }

	private AnimationManager manager;
	private Animation defaultAnimation;
	private LinkedList<Animation> queue;
}
