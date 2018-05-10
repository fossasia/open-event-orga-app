package org.fossasia.openevent.app.data.session;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.serializer.ObservableString;
import org.fossasia.openevent.app.data.event.serializer.ObservableStringDeserializer;
import org.fossasia.openevent.app.data.event.serializer.ObservableStringSerializer;
import org.fossasia.openevent.app.data.tracks.Track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Type("session")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "track")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@EqualsAndHashCode()
@SuppressWarnings("PMD.TooManyFields")
public class Session {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    @Relationship("track")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Track track;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public String title;
    public String subtitle;
    public Integer level;
    public String shortAbstract;
    public String longAbstract;
    public String comments;
    public String language;
    public String slidesUrl;
    public String videoUrl;
    public String audioUrl;
    public String signupUrl;
    public String state;
    public String createdAt;
    public String deletedAt;
    public String submittedAt;
    public String lastModifiedAt;
    public boolean isMailSent;

    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString startsAt = new ObservableString();
    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString endsAt = new ObservableString();
}
