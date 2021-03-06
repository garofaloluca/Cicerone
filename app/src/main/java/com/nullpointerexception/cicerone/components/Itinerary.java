package com.nullpointerexception.cicerone.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 *      Itinerary
 *
 *      Represents a path that a Cicerone want to offer to Globetrotters.
 *
 *      @author Luca
 */
public class Itinerary extends StorableEntity implements StorableAsField, ListOfStorables
{
    /**   Id of itinerary  */
    protected String id,
    /**   City of itinerary  */
            location,
    /**   Date when itinerary will be done  */
            date,
    /**   Location where Cicerone will meet Globetrotters to start itinerary  */
            meetingPlace,
    /**   Time of meeting */
            meetingTime,
    /**   Language spoken during itinerary by Cicerone who is guiding it  */
            language,
    /**   Currency of price of itinerary  */
            currency,
    /**   Description of itinerary  */
            description;

    /**   Cicerone which have created this itinerary  */
    protected User cicerone = new User();

    /**   Price to pay to Cicerone to participate to this itinerary  */
    protected float price;

    /**   Max numbers of participants to this itinerary  */
    protected int maxParticipants;

    /**   List of stages that will be visited into this itinerary  */
    protected List<Stage> stages;

    /**   List of stages proposed from users to this itinerary  */
    protected List<Stage> proposedStages = new ArrayList<>();

    /**   List of users that will participate to this itinerary  */
    protected List<User> participants = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public User getCicerone() {
        return cicerone;
    }

    public void setCicerone(User cicerone) {
        this.cicerone = cicerone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getMeetingTime()
    {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void addPartecipant(User user) {
        participants.add(user);
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Stage> getProposedStages() {
        return proposedStages;
    }

    public void setProposedStages(List<Stage> proposedStages) {
        this.proposedStages = proposedStages;
    }

    public void addProposedStage(Stage stage) {
        proposedStages.add(stage);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *      Generates an id based on its three attributes idCicerone, date and meetingTime
     */
    public void generateId()
    {
        if(cicerone == null)
            cicerone = new User();

        id = cicerone.getId() + date + meetingTime;
        id = id.replace("/", "-").replace(".", "~");
    }

    /**
     *      Get attributes idCicerone, date and meetingTime parsing id
     */
    public void getFieldsFromId()
    {
        if(id == null || id.isEmpty())
            return;

        String id = this.id.replace("~", ".");

        String idCicerone = id.substring(0, id.indexOf("-")-4);
        if(cicerone == null || cicerone.getId() == null || !cicerone.getId().equals(idCicerone))
        {
            cicerone = new User();
            cicerone.setId(idCicerone);
        }
        date = id.substring(idCicerone.length(), idCicerone.length() + 10).replace("-", "/");
        meetingTime = id.substring( idCicerone.length() + date.length() );
    }

    /**
     *      Implementation of its superclass method
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     *      Implementation of its superclass method
     */
    @Override
    public Object addNewInstanceInto(String fieldName)
    {
        if(fieldName.equals("stages"))
        {
            Stage stage = new Stage();

            if(stages == null)
                stages = new Vector<>();

            stages.add(stage);
            return stage;
        }
        else if(fieldName.equals("proposedStages"))
        {
            Stage stage = new Stage();

            if(proposedStages == null)
                proposedStages = new Vector<>();

            proposedStages.add(stage);
            return stage;
        }
        else if(fieldName.equals("participants"))
        {
            User user = new User();

            if(participants == null)
                participants = new Vector<>();

            participants.add(user);
            return user;
        }

        return null;
    }

    /**
     *      Implementation of its superclass method
     */
    @Override
    public List<String> getIgnoredFields()
    {
        return Arrays.asList("id", "meetingTime");
    }

    @Override
    public String getFieldId()
    {
        return id;
    }

    @Override
    public Map<String, String> getSubFields()
    {
        Map<String, String> subFields = new HashMap<>();

        subFields.put("location", location);
        subFields.put("meetingPlace", meetingPlace);

        return subFields;
    }

    @Override
    public void restoreId(String id)
    {
        this.id = id.replace("/", "-");
    }

    @Override
    public void restoreSubFields(Map<String, String> subFields)
    {
        location = subFields.get("location");
        meetingPlace = subFields.get("meetingPlace");
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Itinerary itinerary = (Itinerary) o;
        return Float.compare(itinerary.getPrice(), getPrice()) == 0 &&
                getMaxParticipants() == itinerary.getMaxParticipants() &&
                Objects.equals(getId(), itinerary.getId()) &&
                Objects.equals(getLocation(), itinerary.getLocation()) &&
                Objects.equals(getDate(), itinerary.getDate()) &&
                Objects.equals(getMeetingPlace(), itinerary.getMeetingPlace()) &&
                Objects.equals(getMeetingTime(), itinerary.getMeetingTime()) &&
                Objects.equals(getLanguage(), itinerary.getLanguage()) &&
                Objects.equals(getCurrency(), itinerary.getCurrency()) &&
                Objects.equals(getDescription(), itinerary.getDescription()) &&
                Objects.equals(getCicerone(), itinerary.getCicerone()) &&
                Objects.equals(getStages(), itinerary.getStages()) &&
                Objects.equals(getProposedStages(), itinerary.getProposedStages()) &&
                Objects.equals(getParticipants(), itinerary.getParticipants());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLocation(), getDate(), getMeetingPlace(), getMeetingTime(), getLanguage(), getCurrency(), getDescription(), getCicerone(), getPrice(), getMaxParticipants(), getStages(), getProposedStages(), getParticipants());
    }
}
