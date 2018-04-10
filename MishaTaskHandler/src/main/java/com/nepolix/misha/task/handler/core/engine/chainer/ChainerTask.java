/******************************************************************************
 * Copyright © 2016-7532 HEX, Inc. [7EPOLIX]-(Behrooz Shahriari)              *
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

package com.nepolix.misha.task.handler.core.engine.chainer;

import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.Task;
import com.nepolix.misha.task.handler.core.task.TaskType;

/**
 * @author Behrooz Shahriari
 * @since 10/29/16
 */
public abstract
class ChainerTask < R >
				extends Task
{
	 
	 private Chainer< R > chainer;
	 
	 private long taskId;
	 
	 private R previousResult;
	 
	 public
	 ChainerTask ( )
	 {
			
			super ( TaskType.SINGLE );
	 }
	 
	 
	 void setTaskId ( long taskId )
	 {
			
			this.taskId = taskId;
	 }
	 
	 void setChainer ( Chainer< R > chainer )
	 {
			
			this.chainer = chainer;
	 }
	 
	 ChainerTask setPreviousResult ( R previousResult )
	 {
			
			this.previousResult = previousResult;
			return this;
	 }
	 
	 @Override
	 public
	 void execute ( ITaskEngine iTaskEngine ,
									final TaskListener listener )
	 {
			
			try
			{
				 TaskListener< R > innerListener = new TaskListener< R > ( )
				 {
						
						boolean aBoolean = false;
						
						@Override
						public
						void finish ( )
						{
							 
							 if ( !aBoolean ) chainer.setResult ( null , taskId );
							 listener.finish ( );
						}
						
						@Override
						public
						void setResult ( R result )
						{
							 
							 chainer.setResult ( result , taskId );
							 listener.setResult ( result );
							 aBoolean = true;
						}
				 };
				 task ( previousResult , innerListener );
			}
			catch ( Exception e )
			{
//				 e.printStackTrace ( );
				 JSONObject eJsonObject = JSONException.exceptionToJSON ( e );
				 listener.setResult ( eJsonObject );
				 listener.finish ( );
				 chainer.setError ( eJsonObject , taskId );
			}
	 }
	 
	 protected abstract
	 void task ( R previousResult ,
							 TaskListener listener )
					 throws
					 Exception;
	 
	 
}
