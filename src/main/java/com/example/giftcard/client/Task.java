package com.example.giftcard.client;


import com.example.giftcard.command.api.IssueCmd;
import com.example.giftcard.command.api.RedeemCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;

@Slf4j
public class Task implements Runnable {
    private final CommandGateway commandGateway;
    public Task(CommandGateway c){
        commandGateway = c;
    }

    public void run(){

            UUID id = UUID.randomUUID();
            int issuedAmount = 100;

            log.debug("Sending issue cmd");
            commandGateway.sendAndWait(new IssueCmd(id, issuedAmount));

            log.debug("Sending redeem cmd");
            commandGateway.sendAndWait(new RedeemCmd(id, 40));

            log.debug("Sending redeem cmd");
            commandGateway.sendAndWait(new RedeemCmd(id, 30));

    }
}
