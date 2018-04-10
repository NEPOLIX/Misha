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

import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.Task;
import com.nepolix.misha.task.handler.core.task.callback.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Behrooz Shahriari
 * @since 10/29/16
 */
public
class Chainer < R >
{
	 
	 private final static Random RANDOM = new Random ( );
	 
	 
	 public static
	 < R > Chainer< R > buildChainer ( ITaskEngine iTaskEngine )
	 {
			
			return new Chainer<> ( iTaskEngine );
	 }
	 
	 private
	 Chainer ( ITaskEngine iTaskEngine )
	 {
			
			this.iTaskEngine = iTaskEngine;
			if ( iTaskEngine == null ) this.iTaskEngine = ITaskEngine.buildTaskEngine ( false );
			chainerTasks = new HashMap<> ( );
			cTaskID = new ArrayList<> ( );
	 }
	 
	 private R firstInput;
	 
	 private ITaskEngine iTaskEngine;
	 
	 private HashMap< Long, ChainerTask< R > > chainerTasks;
	 
	 private ArrayList< Long > cTaskID;
	 
	 private Callback< R > finalCallback;
	 
	 public
	 void setFirstInput ( R firstInput )
	 {
			
			this.firstInput = firstInput;
	 }
	 
	 public
	 Chainer< R > first ( R firstInput ,
												final ChainerTask< R > chainerTask )
	 {
			
			this.firstInput = firstInput;
			long taskID = RANDOM.nextLong ( );
			cTaskID.add ( 0 , taskID );
			chainerTasks.put ( taskID , chainerTask );
			chainerTask.setTaskId ( taskID );
			chainerTask.setChainer ( this );
			return this;
	 }
	 
	 public
	 Chainer< R > chain ( final ChainerTask< R > chainerTask )
	 {
			
			long taskID = RANDOM.nextLong ( );
			chainerTask.setTaskId ( taskID );
			chainerTask.setChainer ( this );
			chainerTasks.put ( taskID , chainerTask );
			cTaskID.add ( taskID );
			return this;
	 }
	 
	 public
	 void run ( final Callback< R > finalCallback )
	 {
			
			this.finalCallback = finalCallback;
			runNextTask ( firstInput );
	 }
	 
	 void setResult ( R result ,
										long taskId )
	 {
			
			chainerTasks.remove ( taskId );
			if ( cTaskID.isEmpty ( ) )
			{
				 //run final
				 finalCallback.onResult ( result );
			}
			else runNextTask ( result );
	 }
	 
	 void setError ( JSONObject e ,
									 long taskId )
	 {
			
			chainerTasks.clear ( );
			cTaskID.clear ( );
			if ( finalCallback != null ) finalCallback.onError ( e );
			
	 }
	 
	 private
	 void runNextTask ( R previousResult )
	 {
			
			if ( !cTaskID.isEmpty ( ) )
			{
				 final int idx = 0;
				 long      id  = cTaskID.remove ( idx );
				 
				 Task abstractTask = new Task ( )
				 {
						
						@Override
						public
						void execute ( ITaskEngine iTaskRunner ,
													 TaskListener listener )
						{
							 
							 chainerTasks.get ( id ).setPreviousResult ( previousResult ).execute ( iTaskRunner , listener );
						}
				 };
				 iTaskEngine.add ( abstractTask );
			}
	 }
}
