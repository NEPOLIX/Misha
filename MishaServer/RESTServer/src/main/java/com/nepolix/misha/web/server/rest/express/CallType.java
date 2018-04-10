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

/**
 * @author Behrooz Shahriari
 * @since 10/14/16
 */
public
enum CallType
{
	 GET,
	 POST,
	 PUT,
	 DELETE,
	 UNKNOW;
	 
	 static
	 CallType getCallType ( String requestMethod )
	 {
			
			if ( requestMethod.equals ( "GET" ) ) return GET;
			if ( requestMethod.equals ( "PUT" ) ) return PUT;
			if ( requestMethod.equals ( "POST" ) ) return POST;
			if ( requestMethod.equals ( "DELETE" ) ) return DELETE;
			return UNKNOW;
	 }
}
