package com.example.giftcard.query;


import com.example.giftcard.command.api.IssuedEvt;
import com.example.giftcard.command.api.RedeemCmd;
import com.example.giftcard.command.api.RedeemEvt;
import com.example.giftcard.command.impl.Giftcard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
@Slf4j
//@Profile("query")

public class SummaryProjector {

    private final EntityManager em;

    @EventHandler
    public void on (IssuedEvt evt){
        log.debug("projecting {}", evt);
        em.persist(new GiftcardSummary(evt.getId(), evt.getAmount(), evt.getAmount()));
    }

    @EventHandler
    public void on (RedeemEvt evt){
        log.debug("projecting {}", evt);
        em.find(GiftcardSummary.class, evt.getId()).remainingValue -= evt.getAmount();
    }

    @QueryHandler
    public GiftcardSummary handle(GiftcardSummaryQuery qry){
        return em.find(GiftcardSummary.class, qry.getId());
    }
}
