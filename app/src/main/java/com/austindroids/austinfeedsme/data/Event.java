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
    private String uid;
    private String title;
    private String description;
    private String date;
    private String host;
    private String rsvpLink;
    private String picture;
    private String tags;

    public Event() {}

    public Event(String uid, String title, String description) {
        this.uid = uid;
        this.title = title;
        this.description = description;
    }

    public Event(String uid, String title, String description, String date, String host,
                 String rsvpLink) {

        this.uid = uid;
        this.title = title;
        this.description = description;
        this.date = date;
        this.host = host;
        this.rsvpLink = rsvpLink;
    }

    public Event(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
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

    public String getRsvpLink() {
        return rsvpLink;
    }

    public void setRsvpLink(String rsvpLink) {
        this.rsvpLink = rsvpLink;
    }

//    public boolean isEmpty() {
//        return (title == null || "".equals(title)) &&
//                (description == null || "".equals(description));
//    }

}
