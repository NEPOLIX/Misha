package com.nepolix.misha.task.handler.core.task;


import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;

/**
 * @author Behrooz Shahriari
 * @since 8/8/16
 */
public
interface ITask
{
	 
	 /**
		* @param iTaskEngine
		* 				will be used if need to add more task to pool
		*/
	 void execute ( ITaskEngine iTaskEngine ,
									final TaskListener listener );
	 
	 TaskType getTaskType ( );
	 
	 int getRepetition ( );
	 
	 int taskPriority ( );
}
