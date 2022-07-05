package com.example.giftcard.command.api;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
public class IssuedEvt {
    UUID id;
    int amount;
}
