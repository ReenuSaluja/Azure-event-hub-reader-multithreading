import com.microsoft.azure.eventprocessorhost.*;
import com.microsoft.azure.servicebus.ConnectionStringBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.microsoft.azure.eventhubs.EventData;

public class EventProcessorSample
{
	public static void main(String args[])
    {
		start();
    }
    public static void start()
    {
        final String consumerGroupName = "$Default";

        final String namespaceName = "----ServiceBusNamespaceName-----";
        final String eventHubName = "----EventHubName-----";
        final String sasKeyName = "-----SharedAccessSignatureKeyName-----";
        final String sasKey = "---SharedAccessSignatureKey----";

        final String storageAccountName = "---StorageAccountName----";
        final String storageAccountKey = "---StorageAccountKey----";
        final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=" + storageAccountName + ";AccountKey=" + storageAccountKey;
        
        ConnectionStringBuilder eventHubConnectionString = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);

        
        ExecutorService service = null;
       service = Executors.newWorkStealingPool(2);
       EventProcessorHost host = new EventProcessorHost("Host1",eventHubName,consumerGroupName,eventHubConnectionString.toString(), storageConnectionString,storageAccountName,service);
       
        System.out.println("Registering host named " + host.getHostName());
        EventProcessorOptions options = new EventProcessorOptions();
        options.setExceptionNotification(new ErrorNotificationHandler());
        options.setMaxBatchSize(100);
        
        try
        {
            host.registerEventProcessor(EventProcessor.class, options).get();
        }
        catch (Exception e)
        {
            System.out.print("Failure while registering: ");
            if (e instanceof ExecutionException)
            {
            	
                Throwable inner = e.getCause();
                System.out.println(inner.toString());
            }
            else
            {
                System.out.println(e.toString());
            }
        }

        System.out.println("Press enter to stop");
        try
        {
            System.in.read();
            host.unregisterEventProcessor();

            System.out.println("Calling forceExecutorShutdown");
            EventProcessorHost.forceExecutorShutdown(120);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        System.out.println("End of sample");
    }
}