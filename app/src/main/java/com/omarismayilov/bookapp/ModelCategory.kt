package com.omarismayilov.bookapp

import java.sql.Timestamp

class ModelCategory {
    var id :String = ""
    var category:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()

    constructor(category: String,id: String, timestamp: Long, uid: String) {
        this.category = category
        this.id = id
        this.timestamp = timestamp
        this.uid = uid
    }


}