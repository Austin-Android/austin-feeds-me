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

package com.austindroids.austinfeedsme.data

class Event {

    var id: String? = null
    var name: String? = null
        private set
    var description: String? = null
        private set
    var event_url: String? = null
    var foodType: String? = null
    val picture: String? = null
    val tags: String? = null
    var group: Group? = null
    var time: Long? = null
    var venue: Venue? = null
    var isFood: Boolean = false

    enum class Type {
        PIZZA, BEER, TACO, NONE
    }

    constructor() {}

    constructor(name: String) {
        this.name = name
    }

    constructor(uid: String, name: String, description: String) {
        this.id = uid
        this.name = name
        this.description = description
    }

    constructor(uid: String, name: String, description: String, time: Long?,
                rsvpLink: String, foodType: String) {

        this.id = uid
        this.name = name
        this.description = description
        this.time = time
        this.event_url = rsvpLink
        this.foodType = foodType
    }

    constructor(uid: String, name: String, description: String, time: Long?,
                rsvpLink: String, foodType: String, isFood: Boolean) : this(uid, name, description, time, rsvpLink, foodType) {
        this.isFood = isFood
    }
}
