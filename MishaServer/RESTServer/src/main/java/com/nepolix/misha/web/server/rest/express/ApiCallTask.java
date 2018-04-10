/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to NOX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.web.server.rest.express;

import com.nepolix.misha.task.handler.core.task.callback.Callback;
import com.nepolix.misha.task.handler.core.task.callback.CallbackTask;
import com.nepolix.misha.task.handler.core.task.TaskType;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.web.server.rest.Request;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public abstract
class ApiCallTask
				extends CallbackTask< JSONObject >
{
	 
	 private Request request;
	 
	 public
	 ApiCallTask ( Request request ,
								 Callback< JSONObject > callback )
	 {
			
			super ( TaskType.SINGLE , callback );
			this.request = request;
	 }
	 
	 
}
