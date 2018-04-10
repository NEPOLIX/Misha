import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.Task;
import com.nepolix.misha.task.handler.core.task.callback.Callback;
import com.nepolix.misha.task.handler.core.task.callback.CallbackTask;

import java.util.Random;

/**
 * @author Behrooz Shahriari
 * @since 9/20/16
 */
public
class TestTaskHandler
{
	 
	 public static
	 void main ( String[] args )
	 {
			
			ITaskEngine iTaskEngine = ITaskEngine.buildTaskEngine ( false );
			Callback callback = new Callback ( )
			{
				 
				 @Override
				 public
				 void onResult ( Object result )
				 {
						
						System.out.println ( " -   " + result.toString ( ) );
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
						System.err.println ( e.toString ( ) );
				 }
			};
			iTaskEngine.add ( new CallbackTask< String > ( callback )
			{
				 
				 @Override
				 protected
				 void callBackExecute ( ITaskEngine iTaskEngine ,
																TaskListener listener )
								 throws
								 Exception
				 {
						
						String s = Utils.generateRandomString ( 30 );
						System.out.println ( s );
						listener.setResult ( s );
						listener.finish ( );
				 }
			} );

//			for ( int i = 0 ; i < 100 ; ++i )
//				 new Thread ( new Bomb ( iTaskEngine ) ).start ( );
	 }
	 
}

class Bomb
				implements Runnable
{
	 
	 ITaskEngine iTaskEngine;
	 
	 Bomb ( ITaskEngine iTaskEngine )
	 {
			
			this.iTaskEngine = iTaskEngine;
	 }
	 
	 @Override
	 public
	 void run ( )
	 {
			
			while ( true )
			{
				 try
				 {
						Thread.sleep ( new Random ( ).nextInt ( 600 ) );
				 }
				 catch ( InterruptedException e )
				 {
				 }
				 iTaskEngine.add ( new Task1 ( new Callback< String > ( )
				 {
						
						@Override
						public
						void onResult ( String result )
						{
							 
							 System.out.println ( Utils.getCurrentCodeInfo ( Thread.currentThread ( ) ) + result );
						}
						
						@Override
						public
						void onError ( JSONObject e )
						{
							 
							 System.out.println ( Utils.getCurrentCodeInfo ( Thread.currentThread ( ) ) + e.toString ( ) );
						}
				 } ) );
				 iTaskEngine.add ( new Task3 ( ) );
				 iTaskEngine.add ( new Task4 ( ) );
				 iTaskEngine.add ( new Task2 ( new Callback< String > ( )
				 {
						
						@Override
						public
						void onResult ( String result )
						{
							 
							 System.out.println ( ">" + Utils.getCurrentCodeInfo ( Thread.currentThread ( ) ) + result );
						}
						
						@Override
						public
						void onError ( JSONObject e )
						{
							 
							 System.out.println ( ">" + Utils.getCurrentCodeInfo ( Thread.currentThread ( ) ) + e.toString ( ) );
						}
				 } ) );
			}
	 }
}


class Task1
				extends CallbackTask< String >
{
	 
	 Task1 ( Callback< String > callback )
	 {
			
			super ( callback );
	 }
	 
	 
	 @Override
	 protected
	 void callBackExecute ( ITaskEngine iTaskEngine ,
													TaskListener listener )
					 throws
					 Exception
	 {
			
			String s      = "";
			Random random = new Random ( );
			for ( int i = 0 ; i < random.nextInt ( 100 ) + 10 ; ++i )
				 s += Utils.generateRandomString ( 10 );
			listener.setResult ( s );
			listener.finish ( );
	 }
}

class Task2
				extends CallbackTask< String >
{
	 
	 Task2 ( Callback< String > callback )
	 {
			
			super ( callback );
	 }
	 
	 @Override
	 protected
	 void callBackExecute ( ITaskEngine iTaskEngine ,
													TaskListener listener )
	 {
			
			listener.setResult ( "EMPTY" );
			listener.finish ( );
	 }
}

class Task3
				extends Task
{
	 
	 Task3 ( )
	 {
			
			super ( );
	 }
	 
	 @Override
	 public
	 void execute ( ITaskEngine iTaskEngine ,
									TaskListener listener )
	 {
			
			String s = "";
			for ( int i = 0 ; i < 1400 ; ++i )
				 s += Utils.generateRandomString ( 10 );
			listener.setResult ( s );
			listener.finish ( );
	 }
}

class Task4
				extends Task
{
	 
	 Task4 ( )
	 {
			
			super ( );
	 }
	 
	 @Override
	 public
	 void execute ( ITaskEngine iTaskEngine ,
									TaskListener listener )
	 {
			
			Random random = new Random ( );
			if ( random.nextDouble ( ) < 0.01d )
			{
				 Utils.causeGC ( );
				 try
				 {
						Thread.currentThread ( ).sleep ( 800 );
				 }
				 catch ( InterruptedException e )
				 {
						e.printStackTrace ( );
				 }
			}
			listener.finish ( );
	 }
}