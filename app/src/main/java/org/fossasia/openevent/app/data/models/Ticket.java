package org.fossasia.openevent.app.data.models;

import android.support.annotation.VisibleForTesting;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@Table(database = OrgaDatabase.class, allFields = true)
public class Ticket {
    @PrimaryKey
    public long id;

    public String description;
    public String name;
    public float price;
    public long quantity;
    public String type;

    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public Ticket() {}

    @VisibleForTesting
    public Ticket(long id, long quantity) {
        setId(id);
        setQuantity(quantity);
    }

    @VisibleForTesting
    public Ticket(long quantity, String type) {
        this.quantity = quantity;
        this.type = type;
    }

    @VisibleForTesting
    public Ticket price(float price) {
        this.price = price;
        return this;
    }
}
