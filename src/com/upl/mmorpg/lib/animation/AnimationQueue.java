package com.upl.mmorpg.lib.animation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.upl.mmorpg.game.Game;
import com.upl.mmorpg.game.character.MMOCharacter;
import com.upl.mmorpg.lib.liblog.Log;

public class AnimationQueue implements Collection<Animation>, Serializable
{
	public AnimationQueue(AnimationManager manager, Animation defaultAnimation)
	{
		this.manager = manager;
		this.defaultAnimation = defaultAnimation;
		queue = new LinkedList<Animation>();
		queue.add(defaultAnimation);
	}
	
	/**
	 * Stops all of the animations and clears the
	 * animation queue.
	 */
	public void cleanup()
	{
		for(Animation animation : queue)
			animation.animationStopped();
		queue.clear();
		queue = null;
		defaultAnimation = null;
		manager = null;
	}
	
	/**
	 * Returns the current animation.
	 * @return The current animation.
	 */
	public synchronized Animation getCurrent()
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
			Log.vln("Default animation added to queue.");
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
		Animation current = getCurrent();
		queue.clear();
		queue.add(current);
		queue.add(animation);
		current.interrupt();
		checkDefault();
	}
	
	/**
	 * Move to the next animation.
	 */
	public synchronized void nextAnimation()
	{
		queue.removeFirst();
		checkDefault();
		manager.animationChanged();
		queue.getFirst().animationStarted();
	}
	
	/**
	 * Returns whether or not the default animation is playing.
	 * @return Whether or not the default animation is playing.
	 */
	public synchronized boolean isDefault()
	{
		return getCurrent() == defaultAnimation;
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

	public void updateTransient(Game game, MMOCharacter character, AnimationManager manager)
	{
		this.manager = manager;
		defaultAnimation.updateTransient(game, character, manager);
		
		Iterator<Animation> it = iterator();
		while(it.hasNext())
		{
			Animation animation = it.next();
			animation.updateTransient(game, character, manager);
		}
	}
	
	private transient AnimationManager manager;
	private Animation defaultAnimation;
	private LinkedList<Animation> queue;
	
	private static final long serialVersionUID = -5408433584481058868L;
}
