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

package com.nepolix.misha.db.cold.service.athena;

import com.amazonaws.athena.jdbc.shaded.com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.nepolix.misha.db.cold.ColdDBConstants;

/**
 * @author Behrooz Shahriari
 * @since 2/23/18
 */
public
class AthenaCredentialsProvider
				implements AWSCredentialsProvider
{
	 
	 
	 private BasicAWSCredentials credentials;
	 
	 public
	 AthenaCredentialsProvider ( )
	 {
		
	 }
	 
	 @Override
	 public
	 AWSCredentials getCredentials ( )
	 {
			
			
			if ( credentials == null )
			{
				 credentials = new BasicAWSCredentials ( ColdDBConstants.getAwsAccessKey ( ) , ColdDBConstants.getAwsPrivateKey ( ) );
			}
			return credentials;
	 }
	 
	 @Override
	 public
	 void refresh ( )
	 {
		
	 }
}
