package com.example.giftcard.client;


import com.example.giftcard.command.api.IssueCmd;
import com.example.giftcard.command.api.RedeemCmd;
import com.example.giftcard.query.GiftcardSummary;
import com.example.giftcard.query.GiftcardSummaryQuery;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;

import java.lang.Math;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
//@Profile("client")
public class TestRunner implements CommandLineRunner {

    //    @Autowired
    private final CommandGateway commandGateway;
    //    @Autowired
    private final QueryGateway queryGateway;

    public void controller(int round, UUID id) {
        for (int i = 1; i <= round; ++i) {
            id = UUID.randomUUID();

            log.debug("Sending issue cmd");
            commandGateway.sendAndWait(new IssueCmd(id, 100));

            log.debug("Sending redeem cmd");
            commandGateway.sendAndWait(new RedeemCmd(id, 40));

            log.debug("Sending redeem cmd");
            commandGateway.sendAndWait(new RedeemCmd(id, 30));
        }
    }

    @Override
    public void run(String... args) throws Exception {
//        UUID id = UUID.randomUUID();
        UUID id = null;

        FileWriter file = new FileWriter("/Users/akkharawat.burachokviwat/Desktop/result.csv");
        CSVWriter resultFile = new CSVWriter(file);

        int[] nums = {1, 10, 100, 200, 300, 400};

        for (int round : nums) {
            long start1 = System.nanoTime();
            controller(round, id);
            long end1 = System.nanoTime();

            double elapse = (end1 - start1) / Math.pow(10, 9);
            System.out.println("Elapsed Time in seconds: " + elapse);

            double throughput = round / elapse;
            String[] time = {"loop", Integer.toString(round), "-", Double.toString(elapse), Double.toString(throughput)};
            resultFile.writeNext(time);
        }

        List<Runnable> tasks = new ArrayList<Runnable>();
        int numthreads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(numthreads);

        for (int round : nums) {
            for (int i = 0; i < round; ++i) {
                Runnable r = new Task(commandGateway);
                tasks.add(r);
            }

            long start2 = System.nanoTime();
            for (Runnable task : tasks) {
                pool.execute(task);
            }
            long end2 = System.nanoTime();

            double elapse = (end2 - start2) / Math.pow(10, 9);
            System.out.println("Elapsed Time in seconds: " + elapse);
            
            double throughput = 10 * round / elapse;
            String[] time = {"thread", Integer.toString(round), Integer.toString(numthreads), Double.toString(elapse), Double.toString(throughput)};
            resultFile.writeNext(time);

            tasks.removeAll(tasks);
        }

        resultFile.close();


        log.debug("querying");
        GiftcardSummary summary = queryGateway.query(new GiftcardSummaryQuery(id),
                ResponseTypes.instanceOf(GiftcardSummary.class)).join();
        log.debug("summary queried {}", summary);


    }
}
