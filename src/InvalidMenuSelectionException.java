public class InvalidMenuSelectionException extends Exception{

    InvalidMenuSelectionException(Character message){
        super("Error: " + message + " is an invalid choice, please enter a value listed in the menu.");
    }
}