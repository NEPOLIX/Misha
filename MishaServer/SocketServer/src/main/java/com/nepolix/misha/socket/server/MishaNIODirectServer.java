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

import com.nepolix.misha.socket.SocketChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 11/16/17
 */
public
class MishaNIODirectServer
				implements Runnable
{
	 
	 private final static int NUM_CORES = Runtime.getRuntime ( ).availableProcessors ( );
	 
	 private ServerSocket serverSocket;
	 
	 private Thread[] subThreads;
	 
	 private SocketThread[] socketThreads;
	 
	 private int threadIdx;
	 
	 private int serverPort;
	 
	 private ISocketServerExchange serverExchange;
	 
	 public
	 MishaNIODirectServer ( int serverPort ,
													ISocketServerExchange serverExchange )
					 throws
					 IOException
	 {
			
			this.serverPort = serverPort;
			this.serverExchange = serverExchange;
			init ( );
	 }
	 
	 public
	 void start ( )
	 {
			
			new Thread ( this ).start ( );
	 }
	 
	 
	 private
	 void init ( )
	 {
			
			try
			{
				 serverSocket = new ServerSocket ( serverPort );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
			threadIdx = 0;
			int nt = ( 1 << ( NUM_CORES + 1 ) ) + 32;
			socketThreads = new SocketThread[ nt ];
			subThreads = new Thread[ nt ];
			for ( int i = 0 ; i < subThreads.length ; ++i )
			{
				 socketThreads[ i ] = new SocketThread ( i );
				 subThreads[ i ] = new Thread ( socketThreads[ i ] );
				 subThreads[ i ].start ( );
			}
	 }
	 
	 @Override
	 public
	 void run ( )
	 {
			
			while ( !serverSocket.isClosed ( ) )
			{
				 try
				 {
						Socket        socket        = serverSocket.accept ( );
						SocketChannel socketChannel = new SocketChannel ( socket , -1 );
						socketThreads[ threadIdx ].addSocketChannel ( socketChannel );
						threadIdx++;
						threadIdx %= socketThreads.length;
				 }
				 catch ( IOException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private
	 class SocketThread
					 implements Runnable
	 {
			
			private List< SocketChannel > socketChannels;
			
			private int threadIdx;
			
			SocketThread ( int threadIdx )
			{
				 
				 socketChannels = new ArrayList<> ( );
				 this.threadIdx = threadIdx;
				 socketChannels = new ArrayList<> ( 128 );
			}
			
			List< SocketChannel > getSocketChannels ( )
			{
				 
				 return socketChannels;
			}
			
			void addSocketChannel ( SocketChannel socketChannel )
			{
				 
				 if ( isConnected ( socketChannel ) )
				 {
						getSocketChannels ( ).add ( socketChannel );
						synchronized ( socketChannels )
						{
							 socketChannels.notify ( );
						}
				 }
			}
			
			@Override
			public
			void run ( )
			{
				 
				 int failTO = 0;
				 while ( !serverSocket.isClosed ( ) )
				 {
						synchronized ( socketChannels )
						{
							 if ( socketChannels.isEmpty ( ) )
							 {
									try
									{
										 socketChannels.wait ( );
									}
									catch ( InterruptedException e )
									{
										 e.printStackTrace ( );
									}
							 }
						}
						
						if ( !socketChannels.isEmpty ( ) )
						{
							 SocketChannel socketChannel = getSocketChannels ( ).remove ( 0 );
							 if ( isConnected ( socketChannel ) )
							 {
									String response = "";
									try
									{
										 failTO = failTO < 0 ? 0 : failTO;
										 failTO = failTO > 30 ? 30 : failTO;
										 socketChannel.setTimeOut ( ( failTO + 1 ) * 200 );
										 response = serverExchange.handler ( socketChannel.readMessage ( ) , socketChannel.getInetAddress ( ) );
										 socketChannel.writeMessage ( response );
										 failTO -= ( failTO + 1 ) / 2;
									}
									catch ( Exception e )
									{
										 failTO++;
									}
									finally
									{
										 socketChannel.close ( );
									}
							 }
						}
//						for ( int i = 0 ; i < getSocketChannels ( ).size ( ) ; ++i )
//						{
//							 SocketChannel socketChannel = getSocketChannels ( ).get ( i );
//							 if ( isConnected ( socketChannel ) )
//							 {
//									String response = "";
//									try
//									{
//										 failTO = failTO < 0 ? 0 : failTO;
//										 failTO = failTO > 30 ? 30 : failTO;
//										 socketChannel.setTimeOut ( ( failTO + 1 ) * 200 );
//										 response = serverExchange.handler ( socketChannel.readMessage ( ) , socketChannel.getInetAddress ( ) );
//										 socketChannel.writeMessage ( response );
//										 failTO -= ( failTO + 1 ) / 2;
//									}
//									catch ( SocketTimeoutException ignored )
//									{
//										 failTO++;
//									}
//									catch ( Exception e )
//									{
//										 System.err.println ( "broken connection>> response=" + response );
//										 socketChannel.close ( );
//										 getSocketChannels ( ).remove ( i );
//										 i--;
//									}
//							 }
//							 else
//							 {
//									getSocketChannels ( ).remove ( i );
//									i--;
//							 }
//						}
				 }
			}
			
			private
			boolean isConnected ( SocketChannel socketChannel )
			{
				 
				 return socketChannel != null && socketChannel.isConnected ( );
			}
	 }

//	 private
//	 void accept ( SelectionKey key )
//					 throws
//					 IOException
//	 {
//			// For an accept to be pending the channel must be a server socket channel.
//			ServerSocketChannel serverSocketChannel = ( ServerSocketChannel ) key.channel ( );
//			// Accept the connection and make it non-blocking
//			SocketChannel socketChannel = serverSocketChannel.accept ( );
//			socketChannel.configureBlocking ( false );
//			// Register the new SocketChannel with our Selector, indicating
//			// we'd like to be notified when there's data waiting to be read
//			StringBuilder stream = new StringBuilder ( );
//			socketChannel.register ( selector , SelectionKey.OP_READ , stream );
//	 }
//
//	 private
//	 void read ( SelectionKey key )
//	 {
//
//			SocketChannel socketChannel     = ( SocketChannel ) key.channel ( );
//			InetAddress   socketInetAddress = socketChannel.socket ( ).getInetAddress ( );
//			try
//			{
//				 StringBuilder stream     = ( StringBuilder ) key.attachment ( );
//				 ByteBuffer    readBuffer = ByteBuffer.allocate ( 102400 );
//				 int           numRead;
//				 numRead = socketChannel.read ( readBuffer );
//				 if ( numRead == -1 )
//				 {
//						key.channel ( ).close ( );
//						key.cancel ( );
//				 }
//				 else
//				 {
//						List< String > messages = getMessage ( stream , readBuffer , numRead );
////						System.out.println ( "messages=" + messages.toString ( ) );
//						getTaskEngine ( ).add ( ( ) -> {
//							 for ( String message : messages )
//							 {
////									System.out.println ("message="+ message );
//									String response = serverExchange.handler ( message , socketInetAddress );
//									try
//									{
//										 //write response
//										 String x     = Base64.toBase64 ( response.getBytes ( ) ) + "\n";
//										 byte[] bytes = x.getBytes ( );
//										 int    K     = 1024;
//										 int    len   = bytes.length;
//										 for ( int i = 0 ; i < len - K + 1 ; i += K )
//										 {
//												byte[]     bs     = Arrays.copyOfRange ( bytes , i , i + K );
//												ByteBuffer buffer = ByteBuffer.wrap ( bs );
//												socketChannel.write ( buffer );
//										 }
//										 if ( len % K != 0 )
//										 {
//												byte[]     bs     = Arrays.copyOfRange ( bytes , len - len % K , len );
//												ByteBuffer buffer = ByteBuffer.wrap ( bs );
//												socketChannel.write ( buffer );
//										 }
//									}
//									catch ( Exception e )
//									{
//										 e.printStackTrace ( );
//									}
//							 }
//						} );
//				 }
//			}
//			catch ( IOException e )
//			{
//				 e.printStackTrace ( );
//				 // The remote forcibly closed the connection, cancel
//				 // the selection key and close the channel.
//				 key.cancel ( );
//				 try
//				 {
//						socketChannel.close ( );
//				 }
//				 catch ( IOException ignored )
//				 {
//				 }
//			}
//	 }
//
//	 private
//	 List< String > getMessage ( StringBuilder stream ,
//															 ByteBuffer buffer ,
//															 int numRead )
//	 {
//
//			List< String > list = new ArrayList<> ( );
//			for ( int i = 0 ; i < buffer.array ( ).length && i < numRead ; ++i )
//			{
//				 char c = ( char ) buffer.array ( )[ i ];
//				 if ( c != '\n' )
//				 {
//						stream.append ( c );
//				 }
//				 else
//				 {
//						String x = stream.toString ( );
//						x = new String ( Base64.decodeBase64 ( x ) );
//						list.add ( x );
//						stream.setLength ( 0 );
//				 }
//			}
//			buffer.compact ( );
//			return list;
//	 }
}
