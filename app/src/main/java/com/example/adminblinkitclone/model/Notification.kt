package com.example.adminblinkitclone.model

import com.example.userblinkitclone.models.NotificationData

data class Notification(
    val to : String ?= null,
    val data : NotificationData
)
