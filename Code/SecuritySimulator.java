import MessagePackage.Message;
import MessagePackage.MessageConstants;
import MessagePackage.MessageManagerInterface;
import TermioPackage.Termio;

public class SecuritySimulator {
	static boolean Registered = true;
	static private MessageManagerInterface em = null;	// Interface object to the message manager
	static String MsgMgrIP = null;
	
	public static void main(String[] args) {
		try{
			if (args.length != 0) {
				MsgMgrIP = args[0];
				em = new MessageManagerInterface( MsgMgrIP );
			} else {
				em = new MessageManagerInterface(  );
			}
		}catch (Exception e){
			System.out.println("SecuritySimulator::Error instantiating message manager interface: " + e);
			Registered = false;
		}
		
		if (Registered)
		{
			Termio UserInput = new Termio();	// Termio IO Object
			boolean Done = false;			// Loop termination flag
			while (!Done)
			{
				System.out.println( "\n---------------------" );
				System.out.println( "Security Simulator: \n" );

				if (args.length != 0)
					System.out.println( "Using message manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manger \n" );
				
				System.out.println( "Select an Option: \n" );
				System.out.println( "1: simulate a window break" );
				System.out.println( "2: simulate a door break" );
				System.out.println( "3: simulate a motion");
				System.out.println( "4: simulate a fire");
				System.out.println( "X: Stop System\n" );
				System.out.print( "\n>>>> " );
				String Option = UserInput.KeyboardReadString();

				if ( Option.equals( "1" ) ){
					Message msg = new Message(MessageConstants.ALARM_DATA, MessageConstants.ALARM_WINDOW);
					try{
						em.SendMessage( msg );
					}catch (Exception e){
						System.out.println("Error sending message:: " + e);
					}
					System.out.println("Simulating...");
				}
				if ( Option.equals( "2" ) ){
					Message msg = new Message(MessageConstants.ALARM_DATA, MessageConstants.ALARM_DOOR);
					try{
						em.SendMessage( msg );
					}catch (Exception e){
						System.out.println("Error sending message:: " + e);
					}
					System.out.println("Simulating...");
				}
				if ( Option.equals( "3" ) ){
					Message msg = new Message(MessageConstants.ALARM_DATA, MessageConstants.ALARM_MOTION);
					try{
						em.SendMessage( msg );
					}catch (Exception e){
						System.out.println("Error sending message:: " + e);
					}
					System.out.println("Simulating...");
				}
				if ( Option.equals( "4" ) ){
					Message msg = new Message(MessageConstants.FIRE_DATA, MessageConstants.ALARM_MOTION);
					try{
						em.SendMessage( msg );
					}catch (Exception e){
						System.out.println("Error sending  message:: " + e);
					}
					System.out.println("Simulating...");
				}
				
				if (Option.equalsIgnoreCase("X")){ 
					Halt();
					Done = true;
					System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
					Halt();
				}
			}
		} else {
			System.out.println("\n\nUnable start the monitor.\n\n" );
		}
	}
	
	public static void Halt(){
		System.out.println( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );
		Message msg;
		msg = new Message( MessageConstants.DEVICE_STOP, "XXX" );
		try{
			em.SendMessage( msg );
		}catch (Exception e){
			System.out.println("Error sending halt message:: " + e);
		}
	}
}
