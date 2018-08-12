package bkko.simplecqrs.domain;

import lombok.Data;

@Data
public class Payment {

    private String body;

    public Payment(String body) {
        this.body = body;
    }
}

