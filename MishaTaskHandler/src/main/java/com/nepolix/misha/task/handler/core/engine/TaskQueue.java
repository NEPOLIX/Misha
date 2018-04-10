package com.nepolix.misha.task.handler.core.engine;


import com.nepolix.misha.task.handler.core.task.ITask;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Behrooz Shahriari
 * @since 8/8/16
 */
class TaskQueue
{
	 
	 private ConcurrentLinkedQueue< ITask > queue;
	 
	 
	 public
	 TaskQueue ( )
	 {
			
			queue = new ConcurrentLinkedQueue<> ( );
	 }
	 
	 public
	 void addTask ( ITask ITask )
	 {
			
			queue.add ( ITask );
	 }
	 
	 ITask remove ( )
	 {
			
			return queue.remove ( );
	 }
	 
	 ITask peek ( )
	 {
			
			return queue.peek ( );
	 }
	 
	 boolean isEmpty ( )
	 {
			
			return queue.isEmpty ( );
	 }
	 
	 int size ( )
	 {
			
			return queue.size ( );
	 }
	 
	 void clear ( )
	 {
			
			queue.clear ( );
	 }
}
