package com.example.giftcard;

import com.thoughtworks.xstream.XStream;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class GiftcardApplication {

    public static void main(String[] args) {
        SpringApplication.run(GiftcardApplication.class, args);
    }

    @Autowired
    public void config(Serializer serializer) {
        if (serializer instanceof XStreamSerializer) {
            XStream xStream = ((XStreamSerializer) serializer).getXStream();
            XStream.setupDefaultSecurity(xStream);
            xStream.allowTypesByWildcard(new String[]{
                    "com.example.**",
                    "org.axonframework.**"});
        }
    }

}
