package ostasp.bookapp.order.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {
    NEW{
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            return switch (status){
                case PAID -> UpdateStatusResult.OK(PAID);
                case CANCELED -> UpdateStatusResult.REVOKED(CANCELED);
                case ABANDONED -> UpdateStatusResult.REVOKED(ABANDONED);
                default -> super.updateStatus(status);
            };
        }
    },
    PAID{
        @Override
        public UpdateStatusResult updateStatus(OrderStatus status) {
            if (status == SHIPPED)
                return UpdateStatusResult.OK(SHIPPED);
            return super.updateStatus(status);
        }
    },
    CANCELED,
    ABANDONED,
    SHIPPED;

    public static Optional<OrderStatus> parseString(String value) {
        return Arrays.stream(values()).filter(it -> StringUtils.equalsIgnoreCase(it.name(), value)).findFirst();
    }

    public UpdateStatusResult updateStatus (OrderStatus status){
        throw new IllegalArgumentException("Unable to mark " + this.name() + " order as " + status.name());
    }

}
