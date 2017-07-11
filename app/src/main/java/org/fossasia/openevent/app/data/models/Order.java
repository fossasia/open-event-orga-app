package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class Order {
    @PrimaryKey
    public long id;

    public float amount;
    public String completedAt;
    public String identifier;
    public String invoiceNumber;
    public String paidVia;
    public String  paymentMode;
    public String status;

    public Order() {}

    public Order(String identifier) {
        setIdentifier(identifier);
    }
}
