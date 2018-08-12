package bkko.simplecqrs.domain;

import lombok.Data;

@Data
public class OrderDetail {

    private String body;

    public OrderDetail(String body) {
        this.body = body;
    }
}
