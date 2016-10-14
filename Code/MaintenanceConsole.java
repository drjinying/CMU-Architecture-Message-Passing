/*
 * Author: Ying Jin, Carnegie Mellon University
 * 
 * list all devices
 * detect not responding devices and notify
 * */
import TermioPackage.*;
import MessagePackage.*;

public class MaintenanceConsole
{
	public static void main(String args[]){
    	Termio UserInput = new Termio();	// Termio IO Object
		boolean Done = false;				// Main loop flag
		String Option = null;				// Menu choice from user
		Message Msg = null;					// Message object
		boolean Error = false;				// Error flag
		MaintenanceMonitor Monitor = null;	// The environmental control system monitor


 		if ( args.length != 0 ){
 			Monitor = new MaintenanceMonitor( args[0] );
		} else {
			Monitor = new MaintenanceMonitor();
		}

		if (Monitor.IsRegistered() )
		{
			Monitor.start();
			while (!Done)
			{
				System.out.println( "\n---------------------" );
				System.out.println( "Maintenance Console: \n" );

				if (args.length != 0)
					System.out.println( "Using message manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manger \n" );
				
				System.out.println( "\nCurrent health check inverval: " + MaintenanceMonitor.Delay + " ms\n");
				System.out.println( "Select an Option: \n" );
				System.out.println( "1: list sensors and controllers" );
				System.out.println( "2: Set health check interval" );
				System.out.println( "3: remove device from health check");
				System.out.println( "X: Stop System\n" );
				System.out.print( "\n>>>> " );
				Option = UserInput.KeyboardReadString();

				if ( Option.equals( "1" ) ){
					System.out.println("[ID | Name | Description]:");
					for (String id : MaintenanceMonitor.healthRecord.keySet()) {
						MaintenanceMonitor.HealthCheckRecord record = MaintenanceMonitor.healthRecord.get(id);
						System.out.println(id + "\t" + record.deviceName);
						System.out.println("\t" + record.deviceDesc);
					}
				}
				
				if ( Option.equals( "2" ) ){
					System.out.println( "\nEnter interval in miliseconds >2999 >>>  ");
					String interStr = UserInput.KeyboardReadString();
					try {
						int interval = Integer.parseInt(interStr);
						if (interval >= 3000) {
							MaintenanceMonitor.setDelay(interval);
						} else {
							System.out.println("Interval to small. Should be >= 3000.\n");
						}
					} catch (Exception ex) {
						System.out.println("Interval invalid. Should be an integer.\n");
					}
				}
				
				if ( Option.equals( "3" ) ){
					System.out.println( "\nEnter device id>>>  ");
					String id = UserInput.KeyboardReadString();
					if (MaintenanceMonitor.healthRecord.containsKey(id)) {
						MaintenanceMonitor.healthRecord.remove(id);
						System.out.println( "\nRemoved.\n");
					} else {
						System.out.println( "\nDevice not found.\n");
					}
				}
				if (Option.equalsIgnoreCase("X")){ 
					Monitor.Halt();
					Done = true;
					System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
					Monitor.Halt();
				}
			}
		} else {
			System.out.println("\n\nUnable start the monitor.\n\n" );
		}
  	}
}