package pods.cabs;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Wallet extends AbstractBehavior<Wallet.Command>
{
private int balance;
private int initialBalance;
private int id;


public interface Command{}

 
/*
*  RESPONSE DEFINITION
*/
public static final class ResponseBalance
{

    int balance;
    public  ResponseBalance(int balance)
    {
        this.balance=balance;
    }
}

 /*
     * INITIALIZATION
     */
    public static Behavior<Command> create(int id, int balance) {
        return Behaviors.setup(context->{return new Wallet(context, id, balance);});
    }



    private Wallet(ActorContext<Command> context , int id , int balance) { 
        super(context);
        this.id=id;
        this.balance=0;
        this.initialBalance=balance;
    }

    /*
     * MESSAGE HANDLING
     */

     @Override
     public Receive<Command> createReceive()
     {
         ReceiveBuilder<Command> builder = newReceiveBuilder();

         builder.onMessage(GetBalance.class,    this::onGetBalance);
         builder.onMessage(DeductBalance.class, this::onDeductBalance);
         builder.onMessage(AddBalance.class,    this::onAddBalance);
         builder.onMessage(Reset.class,         this::onReset);

         return builder.build();
     }


     private Behavior<Command> onGetBalance(GetBalance message)
     {
         message.replyTo.tell(new ResponseBalance(this.balance));
         return this;
     }

     private Behavior<Command> onDeductBalance(DeductBalance message)
     {
         if(this.balance-message.toDeduct <0)
         {
             return -1;
         }
         this.balance-=message.toDeduct;
         message.replyTo.tell(new ResponseBalance(this.balance));
         return this;
     }

     private Behavior<Command> onAddBalance(AddBalance message)
     {
         this.balance+=message.balance;
         return this;
     }

     private Behavior<Command> onReset(Reset message)
     {

        this.balance=initialBalance;
        message.replyTo.tell(new ResponseBalance(this.balance));
         return this;
     }



  /*
     * COMMAND DEFINITIONS
     */

public static final class GetBalance implements Command{

    final ActorRef<Wallet.ResponseBalance> replyTo;

    public GetBalance(ActorRef<Wallet.ResponseBalance> replyTo)
    {

        this.replyTo=replyTo;
    }

}


public static final class DeductBalance implements Command
{
   final ActorRef<Wallet.ResponseBalance> replyTo;
    final int toDeduct;
    public void DeductBalance(int toDeduct, ActorRef<Wallet.ResponseBalance> replyTo)
    {

        this.toDeduct=toDeduct;
        this.replyTo=replyTo;
    }
}


public static final class Reset implements Command
{
    final ActorRef<Wallet.ResponseBalance> replyTo;

    public void Reset(ActorRef<Wallet.ResponseBalance> replyTo)
    {
       

        this.replyTo=replyTo;
    }


}

public static final class AddBalance implements Command
{
 
    final int balance;
    public void AddBalance(int toAdd)
    {
        this.balance=toAdd;

    }
}




}

