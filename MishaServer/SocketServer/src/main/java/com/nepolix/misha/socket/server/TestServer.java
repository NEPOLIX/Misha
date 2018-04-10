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

package com.nepolix.misha.socket.server;

import java.io.IOException;

/**
 * @author Behrooz Shahriari
 * @since 11/17/17
 */
public
class TestServer
{
	 
	 public static
	 void main ( String[] args )
					 throws
					 IOException
	 {
			
			ISocketServerExchange handler = ( x , z ) -> x;
			MishaNIODirectServer  server  = new MishaNIODirectServer ( 23572 , handler );
			server.start ( );
	 }
}
