/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.task.handler.core.engine;

import com.nepolix.misha.task.handler.core.task.ITask;
import com.nepolix.misha.task.handler.core.task.IxTask;

/**
 * @author Behrooz Shahriari
 * @since 8/8/16
 */
public
interface ITaskEngine
{
	 
	 
	 /**
		* used for infinite loop, N-loop, or put new tasks upon completion of a task
		*
		* @param newITask
		*/
	 ITaskEngine add ( ITask newITask );
	 
	 ITaskEngine add ( IxTask task );
	 
	 boolean isRunning ( );
	 
	 void stop ( );
	 
	 static
	 ITaskEngine buildTaskEngine ( boolean minimalThreads )
	 {
			
			return buildTaskEngine ( minimalThreads , null );
	 }
	 
	 static
	 ITaskEngine buildTaskEngine ( boolean minimalThreads ,
																 TaskExceptionListener exceptionListener )
	 {
			
			return new TaskEngine ( minimalThreads , exceptionListener );
	 }
	 
	 void setExceptionListener ( TaskExceptionListener exceptionListener );
	 
	 void clearTasks ( );
	 
	 int queueSize ( );
}
