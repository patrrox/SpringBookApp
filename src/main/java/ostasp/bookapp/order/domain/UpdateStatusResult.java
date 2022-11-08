package ostasp.bookapp.order.domain;

import lombok.Value;

@Value
public class UpdateStatusResult {

    OrderStatus newStatus;
    boolean revoked;

    static UpdateStatusResult OK (OrderStatus newStatus){
        return new UpdateStatusResult(newStatus,false);
    }

    static UpdateStatusResult REVOKED (OrderStatus newStatus){
        return new UpdateStatusResult(newStatus,true);
    }

}
