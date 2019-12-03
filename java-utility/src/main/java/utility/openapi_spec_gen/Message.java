package utility.openapi_spec_gen;

import org.springframework.stereotype.Component;

@Component
public class Message {

   private String message = "Hello there!";

   public void setMessage(String message){

      this.message  = message;
   }

   public String getMessage(){

      return message;
   }
}