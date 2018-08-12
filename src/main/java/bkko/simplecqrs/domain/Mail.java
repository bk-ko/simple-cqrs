package bkko.simplecqrs.domain;

import lombok.Data;

@Data
public class Mail {

    private String body;

    public Mail(String body) {
        this.body = body;
    }
}
