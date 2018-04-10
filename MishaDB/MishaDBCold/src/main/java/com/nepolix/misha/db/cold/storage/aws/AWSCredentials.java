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

package com.nepolix.misha.db.cold.storage.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.nepolix.misha.db.cold.ColdDBConstants;

/**
 * @author Behrooz Shahriari
 * @since 4/3/17
 */

public
class AWSCredentials
{
	 
	 
	 private static BasicAWSCredentials AWS_CREDENTIALS = null;
	 
	 
	 public static
	 BasicAWSCredentials getAwsCredentials ( )
	 {
			
			if ( ColdDBConstants.getAwsAccessKey ( ) == null || ColdDBConstants.getAwsPrivateKey ( ) == null ) throw new NullPointerException ( "first set access-key and private-key" );
			if ( AWS_CREDENTIALS == null ) AWS_CREDENTIALS = new BasicAWSCredentials ( ColdDBConstants.getAwsAccessKey ( ) , ColdDBConstants.getAwsPrivateKey ( ) );
			return AWS_CREDENTIALS;
	 }
	 
}
