package com.nepolix.misha.task.handler.core.task;

/**
 * @author Behrooz Shahriari
 * @since 8/8/16
 */
public abstract
class Task
				implements ITask,
									 Comparable< ITask >
{
	 
	 protected int repetition;
	 
	 protected TaskType taskType;
	 
	 public
	 Task ( )
	 {
			
			this ( TaskType.SINGLE );
	 }
	 
	 public
	 Task ( TaskType taskType )
	 {
			
			this.taskType = taskType;
			if ( taskType == TaskType.SINGLE ) repetition = 0;
			else
				 if ( taskType == TaskType.INFINITE ) repetition = -1;
				 else throw new IllegalArgumentException ( "ITask Type must be INFINITE or SINGLE" );
	 }
	 
	 public
	 Task ( TaskType taskType ,
					int repetition )
	 {
			
			this.taskType = taskType;
			if ( taskType == TaskType.SINGLE ) repetition = 0;
			if ( taskType == TaskType.INFINITE ) repetition = -1;
			this.repetition = repetition;
	 }
	 
	 
	 @Override
	 public
	 TaskType getTaskType ( )
	 {
			
			return taskType;
	 }
	 
	 @Override
	 public
	 int getRepetition ( )
	 {
			
			return repetition;
	 }
	 
	 public
	 void tickRepetition ( )
	 {
			
			repetition--;
	 }
	 
	 public
	 boolean isInfiniteRecurrent ( )
	 {
			
			return repetition == -1 && taskType == TaskType.RECURRENT;
	 }
	 
	 @Override
	 public
	 int taskPriority ( )
	 {
			
			return 0;
	 }
	 
	 @Override
	 public
	 int compareTo ( ITask o )
	 {
			
			return Integer.compare ( o.taskPriority ( ) , taskPriority ( ) );
	 }
}
