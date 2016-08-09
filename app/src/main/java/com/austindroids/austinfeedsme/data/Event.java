/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.austindroids.austinfeedsme.data;

public final class Event {

    private String id;
    private String uid;
    private String name;
    private String description;
    private String date;
    private String host;
    private String event_url;
    private String picture;
    private String tags;
    private Group group;
    private Long time;
    private Venue venue;

    private boolean isFood;

    public Event() {}

    public Event(String uid, String name, String description) {
        this.uid = uid;
        this.name = name;
        this.description = description;
    }

    public Event(String uid, String name, String description, String date, String host,
                 String rsvpLink) {

        this.uid = uid;
        this.name = name;
        this.description = description;
        this.date = date;
        this.host = host;
    }

    public Event(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getHost() {
        return host;
    }

    public String getPicture() {
        return picture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isFood() {
        return isFood;
    }

    public void setFood(boolean food) {
        isFood = food;
    }

    public Group getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public void setEvent_url(String event_url) {
        this.event_url = event_url;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public String getEvent_url() {
        return event_url;
    }

    public Venue getVenue() {
        return venue;
    }
}
